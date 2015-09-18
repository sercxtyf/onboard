/*******************************************************************************
 * Copyright [2015] [Onboard team of SERC, Peking University]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.onboard.service.account.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.onboard.domain.mapper.CompanyMapper;
import com.onboard.domain.mapper.DepartmentMapper;
import com.onboard.domain.mapper.ProjectMapper;
import com.onboard.domain.mapper.UserCompanyMapper;
import com.onboard.domain.mapper.UserMapper;
import com.onboard.domain.mapper.UserProjectMapper;
import com.onboard.domain.mapper.base.BaseMapper;
import com.onboard.domain.mapper.model.DepartmentExample;
import com.onboard.domain.mapper.model.ProjectExample;
import com.onboard.domain.mapper.model.UserCompanyExample;
import com.onboard.domain.mapper.model.UserExample;
import com.onboard.domain.mapper.model.UserProjectExample;
import com.onboard.domain.model.Company;
import com.onboard.domain.model.Department;
import com.onboard.domain.model.Project;
import com.onboard.domain.model.User;
import com.onboard.domain.model.UserCompany;
import com.onboard.domain.model.UserProject;
import com.onboard.service.account.UserService;
import com.onboard.service.account.redis.Repository;
import com.onboard.service.account.redis.TokenType;
import com.onboard.service.account.utils.PasswordUtils;
import com.onboard.service.base.AbstractBaseService;
import com.onboard.service.email.EmailService;
import com.onboard.service.email.TemplateEngineService;
import com.onboard.service.file.ImageService;

@Transactional
@Service("userServiceBean")
public class UserServiceImpl extends AbstractBaseService<User, UserExample> implements UserService {

    public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String FORGET_PASSWORD_TPL = "templates/ForgetPassword.html";
    private static final String CONFIRMAION_TPL = "templates/Confirmation.html";

    @Autowired
    private AccountConfigure configurer;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserCompanyMapper userCompanyMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private UserProjectMapper userProjectMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TemplateEngineService templateEngineService;

    @Autowired
    private ImageService fileService;

    @Autowired
    private Repository repository;

    private final Function<UserCompany, User> userCompanyMapping = new Function<UserCompany, User>() {
        @Override
        public User apply(UserCompany input) {
            return getById(input.getUserId());
        }
    };

    @Override
    public User signUp(User user, String companyName) {
        assert(!isEmailRegistered(user.getEmail()));
        // create new user
        Date current = new Date();
        user.setNewPassword(PasswordUtils.createPassword(user.getPassword(), current.toString()));
        user.setPassword(null);
        user.setCreated(current);
        user.setUpdated(user.getCreated());
        user.setActivated(false);
        userMapper.insertSelective(user);

        // create a company belonged to the user
        Company company = new Company();
        company.setCreated(new Date());
        company.setDeleted(false);
        company.setCreatorId(user.getId());
        company.setName(companyName);
        company.setPrivileged(false);
        companyMapper.insertSelective(company);

        // add creator to the company
        UserCompany userCompany = new UserCompany();
        userCompany.setUserId(user.getId());
        userCompany.setCompanyId(company.getId());
        userCompanyMapper.insert(userCompany);

        this.sendConfirmationEmail(user);
        return user;
    }

    @Override
    public User getUserWithPasswordByEmail(String email) {
        User sample = new User();
        sample.setEmail(email);
        UserExample example = new UserExample(sample);
        List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            return null;
        }
        return new User(users.get(0));
    }

    @Override
    public User getUserByEmailOrUsernameWithPassword(String emailOrUsername) {
        UserExample example = new UserExample();
        example.or().andEmailEqualTo(emailOrUsername);
        example.or().andUsernameEqualTo(emailOrUsername);
        List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            return null;
        }
        return new User(users.get(0));
    }

    @Override
    public User login(String email, String password) {
        logger.debug("Current Thread Class Loader is {}", Thread.currentThread().getContextClassLoader());
        User user = this.getUserByEmailOrUsernameWithPassword(email);
        if (user != null) {
            String salt = user.getCreated().toString();
            return !PasswordUtils.isPasswordValid(user.getNewPassword(), password, salt) ? null : user;
        }
        return null;
    }

    @Override
    public boolean isEmailRegistered(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }

        return getUserByEmail(email) != null;
    }

    @Override
    public void forgetPassword(String email) {
        User user = getUserByEmail(email);
        String token = repository.addToken(TokenType.FORGET_PASSWORD, user.getId(), configurer.getTokenExpired());

        Map<String, Serializable> model = ImmutableMap.of("host", configurer.getProtocol().concat(configurer.getHost()), "user", user,
                "token", token);
        String text = this.templateEngineService.process(getClass(), FORGET_PASSWORD_TPL, model);
        emailService.sendEmail(email, null, null, "[OnBoard]忘记密码", text, null);
    }

    @Override
    public boolean resetPassword(int uid, String password, String token) {
        if (!repository.authenticateToken(TokenType.FORGET_PASSWORD, uid, token)) {
            return false;
        }
        User user = new User(uid);
        String salt = userMapper.selectByPrimaryKey(user.getId()).getCreated().toString();
        user.setNewPassword(PasswordUtils.createPassword(password, salt));
        userMapper.updateByPrimaryKeySelective(user);
        repository.delToken(TokenType.FORGET_PASSWORD, uid);
        return true;
    }

    @Override
    public User getById(int id) {
        User origin = userMapper.selectByPrimaryKey(id);
        if (origin == null) {
            return null;
        }
        User user = new User(origin);
        user.setPassword(null);
        user.setNewPassword(null);

        return user;
    }

    /**
     * 存在n+1问题
     */
    @Override
    public List<User> getUserByProjectId(int projectId) {

        UserProject sample = new UserProject();
        sample.setProjectId(projectId);
        UserProjectExample example = new UserProjectExample(sample);
        List<UserProject> userProjectList = userProjectMapper.selectByExample(example);
        Function<UserProject, User> mapping = new Function<UserProject, User>() {
            @Override
            public User apply(UserProject input) {
                return getById(input.getUserId());
            }
        };
        return Lists.transform(userProjectList, mapping);
    }

    @Override
    public Map<Department, List<User>> getDepartmentedUserByCompanyId(Integer companyId) {
        Department sample = new Department();
        sample.setCompanyId(companyId);
        DepartmentExample example = new DepartmentExample(sample);
        List<Department> groups = departmentMapper.selectByExample(example);
        Map<Department, List<User>> map = new TreeMap<Department, List<User>>(new Comparator<Department>() {
            @Override
            public int compare(Department o1, Department o2) {
                if (o1.getCustomOrder() == null || o2.getCustomOrder() == null) {
                    return o1.getId().compareTo(o2.getId());
                } else {
                    return o1.getCustomOrder().compareTo(o2.getCustomOrder());
                }
            }
        });

        for (Department g : groups) {
            if (!map.containsKey(g)) {
                map.put(g, getUserByCompanyIdByDepartmentId(g.getId(), companyId));
            }
        }
        return map;
    }

    @Override
    public List<User> getUnDepartmentedUsersByCompanyId(Integer companyId) {
        UserCompanyExample example = new UserCompanyExample();
        example.or().andCompanyIdEqualTo(companyId).andGroupIdIsNull();
        List<UserCompany> userCompanies = userCompanyMapper.selectByExample(example);
        List<User> unDepartmentedUsers = new ArrayList<User>();
        for (UserCompany userCompany : userCompanies) {
            unDepartmentedUsers.add(this.getById(userCompany.getUserId()));
        }

        return unDepartmentedUsers;
    }

    @Override
    public User getUserByEmail(String email) {
        User sample = new User();
        sample.setEmail(email);
        UserExample example = new UserExample(sample);
        List<User> users = userMapper.selectByExample(example);
        if (users.isEmpty()) {
            return null;
        }
        User user = new User(users.get(0));
        user.setPassword(null);
        user.setNewPassword(null);
        return user;
    }

    @Override
    public void updateUser(User user, byte[] avatar, String filename) {
        Preconditions.checkNotNull(user);
        if (avatar != null && avatar.length > 0) {
            String path = Joiner.on("/").join("/avatar", user.getId(), filename);
            fileService.writeFile(path, avatar);
            user.setAvatar(path);
        }
        if (Strings.isNullOrEmpty(user.getNewPassword())) {
            user.setNewPassword(null);
        } else {
            String salt = userMapper.selectByPrimaryKey(user.getId()).getCreated().toString();
            user.setNewPassword(PasswordUtils.createPassword(user.getNewPassword(), salt));
        }
        user.setUpdated(new Date());
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void sendConfirmationEmail(User user) {
        if (user.getActivated()) {
            return;
        }
        String token = repository.addToken(TokenType.CONFIRMATION, user.getId(), configurer.getTokenExpired());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("host", configurer.getProtocol() + configurer.getHost());
        model.put("user", user);
        model.put("token", token);

        String text = this.templateEngineService.process(getClass(), CONFIRMAION_TPL, model);
        emailService.sendEmail(user.getEmail(), null, null, "[OnBoard]注册确认", text, null);
    }

    @Override
    public boolean confirmRegisteredUser(int uid, String token) {
        boolean result = repository.authenticateToken(TokenType.CONFIRMATION, uid, token);
        if (result) {
            User user = new User(uid);
            user.setActivated(true);
            userMapper.updateByPrimaryKeySelective(user);
            repository.delToken(TokenType.CONFIRMATION, uid);
        }
        return result;
    }

    @Override
    public List<User> getUserByCompanyId(int companyId) {
        // TODO 存在N+1问题：即先查一次UserCompany表然后再查N次User表，这样对效率有影响 
        UserCompany sample = new UserCompany();
        sample.setCompanyId(companyId);
        UserCompanyExample example = new UserCompanyExample(sample);
        List<UserCompany> userCompanyList = userCompanyMapper.selectByExample(example);
        List<User> users = Lists.newArrayList();
        for (UserCompany userCompany : userCompanyList) {
            users.add(getById(userCompany.getUserId()));
        }
        return users;
    }

    /**
     * 获取一个分组内所有的用户
     * @param groupId
     * @return
     */
    @Override
    // TODO 函数名顺序和参数顺序不一致
    public List<User> getUserByCompanyIdByDepartmentId(int groupId, int companyId) {
        UserCompany sample = new UserCompany();
        sample.setDepartmentId(groupId);
        sample.setCompanyId(companyId);
        UserCompanyExample example = new UserCompanyExample(sample);
        List<UserCompany> userCompanyList = userCompanyMapper.selectByExample(example);
        List<User> users = Lists.newArrayList();
        for (UserCompany userCompany : userCompanyList) {
            users.add(getById(userCompany.getUserId()));
        }
        return users;
    }

    @Override
    public Map<Integer, List<User>> getAllProjectUsersInCompany(int companyId) {
        Project sample = new Project(false);
        sample.setArchived(false);
        sample.setCompanyId(companyId);
        ProjectExample example = new ProjectExample(sample);
        List<Project> projects = projectMapper.selectByExample(example);
        Map<Integer, List<User>> map = new HashMap<Integer, List<User>>();

        for (Project project : projects) {
            map.put(project.getId(), getUserByProjectId(project.getId()));
        }
        return map;
    }

    @Override
    public boolean isUserInCompany(int userId, int companyId) {
        UserCompany sample = new UserCompany();
        sample.setUserId(userId);
        sample.setCompanyId(companyId);

        return userCompanyMapper.countByExample(new UserCompanyExample(sample)) > 0;
    }

    @Override
    public boolean isUserInProject(int userId, int companyId, int projectId) {
        UserProject sample = new UserProject();
        sample.setUserId(userId);
        sample.setCompanyId(companyId);
        sample.setProjectId(projectId);

        return userProjectMapper.countByExample(new UserProjectExample(sample)) > 0;
    }

    @Override
    public User getUserByEmailOrUsername(String emailOrUsername) {
        User user = getUserByEmailOrUsernameWithPassword(emailOrUsername);
        if (user == null) {
            return null;
        }
        user.setPassword(null);
        user.setNewPassword(null);
        return user;
    }

    @Override
    public Boolean containUsername(String username) {
        User user = new User();
        user.setUsername(username);

        return userMapper.countByExample(new UserExample(user)) > 0;
    }

    /**
     * @author Chenlong to migrate password encoding schema
     */
    @PostConstruct
    public void updatePassword() {
        UserExample example = new UserExample();
        List<User> users = userMapper.selectByExample(example);
        for (User user : users) {
            if (user.getNewPassword() == null) {
                user.setNewPassword(PasswordUtils
                        .updateOldEncPass(user.getPassword().toUpperCase(), user.getCreated().toString()));
                userMapper.updateByPrimaryKeySelective(user);
            }
        }
    }

    @Override
    public String createPassword(String password, String salt) {
        return PasswordUtils.createPassword(password, salt);
    }

    @Override
    public List<User> filterProjectMembers(List<User> users, int projectId) {
        List<User> members = getUserByProjectId(projectId);
        SetView<User> intersection = Sets.intersection(new HashSet<User>(users), new HashSet<User>(members));
        List<User> legalUsers = Lists.newArrayList();
        for (User user : intersection)
            legalUsers.add(user);
        return legalUsers;
    }

    @Override
    protected BaseMapper<User, UserExample> getBaseMapper() {
        return userMapper;
    }

    @Override
    public User newItem() {
        return new User();
    }

    @Override
    public UserExample newExample() {
        return new UserExample();
    }

    @Override
    public UserExample newExample(User item) {
        return new UserExample(item);
    }
}
