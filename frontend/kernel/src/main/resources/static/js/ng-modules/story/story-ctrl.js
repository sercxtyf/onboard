/**
 * Created by pkutxq on 15-5-22.
 */
angular.module('stories')
.controller('storyCtrl',['$scope','$http','url','richtexteditor','tab','storyService','$sce','drawer',
        function($scope,$http,url,richtexteditor,tab,storyService,$sce,drawer){
        $scope.story_form = null;

        $scope.priorities = storyService.priorities;

        $scope.setPriority = function(priority) {
            $scope.story.priority = priority.value;
            $scope.story_form.priority = priority.value;
            $scope.updateStory();
        };
        storyService.getStoryById(true, $scope.projectId, $scope.companyId, $scope.id).then(function(story){
            $scope.story = story;
            $scope.story_form = story;
            if($scope.story.acceptanceLevel == null || $scope.story.acceptanceLevel == ''){
                $scope.story.acceptanceLevel = initAcce;
            }
            if($scope.story.content == null || $scope.story.content == ''){
                $scope.story.content = initCont;
            }

            $scope.$broadcast('updateTab', {
                attachType: "story",
                attachId  : $scope.story.id,
                projectId : $scope.story.projectId,
                companyId : $scope.story.companyId
            });
        },function(error){
        });

        $scope.updateTitle = function(){
            $scope.story_form.description=$scope.story.description;
            $scope.updateStory();
        };

        $scope.updateStory = function(){
            $scope.story.update()
                .then(function(){})
                .catch(function(){
                    $scope.state = 'error';
                    $scope.message = '更新需求失败';
                });
        };
        // 上传附件 图片
        // $scope.uploadedImageInTextEditor = [];

        $scope.storyHtml = function(htmlString) {
            return $sce.trustAsHtml(htmlString);
        };
        $scope.generateHtml = function(pre, description, post) {
            return $sce.trustAsHtml(
                "<ul>" +
                "<li>作为" + (pre ? pre : "……") + "，</li>" +
                "<li>我需要能" + (description ? description : "……") + "，</li>" +
                "<li>这样我就可以" + (post ? post : "……") + "。</li>" +
                "</ul>"
            );
        }

        var windowHeight = document.documentElement.clientHeight*0.1;
        if(windowHeight < 100) windowHeight = 100;

        $scope.showContent = false;
        var storyContentDiv = $('#detailDescriptionForm').find('textarea[name="content"]');
        $scope.editContent = function() {
            richtexteditor.initEditAreaFullTools(storyContentDiv, { height: windowHeight}, null);
            $(storyContentDiv).code($scope.story.content);
            $scope.showContent = true;
        };
        $scope.cancelEditContent = function() {
            storyContentDiv.destroy();
            $scope.showContent= false;
        };


        $scope.showAcceptanceLevel = false;
        var storyAcceptanceLevelDiv = $('#checkRuleForm').find('textarea[name="acceptanceLevel"]');
        $scope.editAcceptanceLevel = function(){
            richtexteditor.initEditAreaFullTools(storyAcceptanceLevelDiv, { height: windowHeight }, null);
            $(storyAcceptanceLevelDiv).code($scope.story.acceptanceLevel);
            $scope.showAcceptanceLevel = true;
        };

        $scope.cancelEditAcceptanceLevel = function(){
            storyAcceptanceLevelDiv.destroy();
            $scope.showAcceptanceLevel = false;
        };

        $scope.saveStoryForm = function(type) {
            if(type !== 'checkRule' && type !== 'detailDescription'){
                return;
            }
            if(type === 'checkRule'){
                $scope.story.acceptanceLevel = $('#checkRule').code();
                storyAcceptanceLevelDiv.destroy();
                $scope.showAcceptanceLevel = false; 
            }
            if(type === 'detailDescription'){
                $scope.story.content = $('#detailDescription').code();
                storyContentDiv.destroy();
                $scope.showContent = false; 
            }
            $scope.story.update().then(function(updateStory){
            }).catch(function(error){
                $scope.acceptRulestate = 'error';
                $scope.ruleMessage = '更新需求失败';
            });
        };
        $scope.setComplete = function(story,complete){
          if(complete){
              for(i=0; i<story.childStoryDTOs.length; i++) {
                  if(!story.childStoryDTOs[i].completed){
                      $scope.state="error";
                      $scope.message = "您有未完成的需求，请完成之后再完成该需求。";
                      return ;
                  }
              }
              storyService.completeStory($scope.story).then(function(response){
                  drawer.close();
              });
          }else{
              $scope.story.completed = false;
              $scope.story.update().then(function(response){
                  drawer.close();
              });
          }
        };
        $scope.storyTabs = [
            tab.getTabInfo("comment", true),
            tab.getTabInfo("linkedstory"),
            tab.getTabInfo("linkedtask"),
            tab.getTabInfo("activity")
        ];
    }]);