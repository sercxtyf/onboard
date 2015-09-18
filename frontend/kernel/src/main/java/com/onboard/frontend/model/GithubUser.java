package com.onboard.frontend.model;

public class GithubUser {
    private String id;
    private String login;
    private String repos_url;
    private String email;
    private String url;
    private String total_private_repos;
    private String public_repos;
    private String owned_private_repos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTotal_private_repos() {
        return total_private_repos;
    }

    public void setTotal_private_repos(String total_private_repos) {
        this.total_private_repos = total_private_repos;
    }

    public String getPublic_repos() {
        return public_repos;
    }

    public void setPublic_repos(String public_repos) {
        this.public_repos = public_repos;
    }

    public String getOwned_private_repos() {
        return owned_private_repos;
    }

    public void setOwned_private_repos(String owned_private_repos) {
        this.owned_private_repos = owned_private_repos;
    }

}
