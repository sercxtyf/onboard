(function(angular, undefined) {
	var module = angular.module('angularTreeview', []);

	module.value('treeViewDefaults', {
		foldersProperty: 'folders',
		filesProperty  : 'files',
		displayProperty: 'name',
		collapsible    : true,
		allExpanded    : false
	});

	module.directive('treeView', ['$q', 'treeViewDefaults', function($q, treeViewDefaults) {
		return {
			restrict  : 'A',
			scope     : {
				treeView       : '=',
				treeViewOptions: '=',
				foldersProperty: '@',
				filesProperty  : '@',
				allExpanded    : '='
			},
			replace   : true,
			template  : '<div class="tree"><div tree-view-folder="treeView"></div></div>',
			controller: ['$scope', function($scope, attrs) {
				var self = this,
					selectedFolder,
					selectedFile;
				this.options = angular.extend({}, treeViewDefaults, $scope.treeViewOptions);
				if ($scope.foldersProperty)
					this.options.foldersProperty = $scope.foldersProperty;
				if ($scope.filesProperty)
					this.options.filesProperty = $scope.filesProperty;
				if ($scope.allExpanded)
					this.options.allExpanded = $scope.allExpanded;

				self.selectFolder = function(folder, breadcrumbs) {
					selectedFolder = folder;
					selectedFile = undefined;
					if (typeof $scope.treeViewOptions.onFolderSelect === "function") {
						$scope.treeViewOptions.onFolderSelect(folder, breadcrumbs);
					}
				};
				self.selectFile = function(file, breadcrumbs) {
					selectedFolder = undefined;
					selectedFile = file;
					if (typeof $scope.treeViewOptions.onFileSelect === "function") {
						$scope.treeViewOptions.onFileSelect(file, breadcrumbs);
					}
				};
				self.isSelected = function(node) {
					return node === selectedFolder || node === selectedFile;
				};
			}]
		};
	}]);

	module.directive('treeViewFolder', ['$q', '$compile', function($q, $compile) {
		return {
			restrict: 'A',
			require : '^treeView',
			link    : function(scope, element, attrs, controller) {
				var options         = controller.options,
					foldersProperty = options.foldersProperty,
					filesProperty   = options.filesProperty,
					displayProperty = options.displayProperty,
					collapsible     = options.collapsible;

				scope.expanded =  options.allExpanded;

				scope.getFolderIconClass = function(folder) {
					return typeof scope.treeViewOptions.folderIcon === 'function' ?
						scope.treeViewOptions.folderIcon(folder, scope.expanded) :
						'treeview-folder-icon' + scope.expanded ? ' treeview-folder-expanded-icon' : '';
				};

				scope.getFileIconClass = function(file) {
					return typeof scope.treeViewOptions.fileIcon === 'function' ?
						scope.treeViewOptions.fileIcon(file) :
						'treeview-file-icon';
				};

				scope.hasChildren = function() {
					var folder = scope.node;
					return Boolean(folder && (folder[foldersProperty] && folder[foldersProperty].length) || (folder[filesProperty] && folder[filesProperty].length));
				};

				scope.selectFolder = function(event) {
					event.preventDefault();
					if (collapsible) toggleExpanded();

					var breadcrumbs = [];
					var nodeScope = scope;
					while (nodeScope.folder) {
						breadcrumbs.push(nodeScope.folder[displayProperty]);
						nodeScope = nodeScope.$parent;
					}
					controller.selectFolder(scope.node, breadcrumbs.reverse());
				};

				scope.selectFile = function(file, event) {
					event.preventDefault();
					//if(collapsible) toggleExpanded();

					var breadcrumbs = [file[displayProperty]];
					var nodeScope = scope;
					while (nodeScope.folder) {
						breadcrumbs.push(nodeScope.folder[displayProperty]);
						nodeScope = nodeScope.$parent;
					}
					controller.selectFile(file, breadcrumbs.reverse());
				};
				scope.isSelected = function(node) {
					return controller.isSelected(node);
				};
				function toggleExpanded() {
					//if (!scope.hasChildren()) return;
					scope.expanded = !scope.expanded;
				}

				function singleJoin(folder) {
					var folders = folder[foldersProperty];

					// leaf node
					if (!folders || folders.length == 0) return;

					// internal node
					for (var i in folders) singleJoin(folders[i]);
					if (folders.length == 1) {
						var child = folders[0];
						folder[displayProperty] += '/' + child.name;
						folder[foldersProperty] = child[foldersProperty];
						folder[filesProperty] = child[filesProperty];
					}
				}

				scope.$watch(attrs.treeViewFolder, function(v) {
					if (v) {
						for (var i in v.folders) {
							singleJoin(v.folders[i]);
						}
					}
				});
				function render() {
					var template =
						'<div class="tree-folder" ng-repeat="folder in ' + attrs.treeViewFolder + '.' + foldersProperty + '">' +
						'<a href="#" class="tree-folder-header inline" ng-click="selectFolder($event)" ng-class="{ selected: isSelected(folder) }">' +
						'<span class="tree-folder-name"><i ng-class="getFolderIconClass(folder)"></i> {{ folder.' + displayProperty + ' }}</span> ' +
						'</a>' +
						'<div class="tree-folder-content"' + (collapsible ? ' ng-show="expanded"' : '') + '>' +
						'<div tree-view-folder="folder">' +
						'</div>' +
						'</div>' +
						'</div>' +
						'<a href="#" class="tree-item" ng-repeat="file in ' + attrs.treeViewFolder + '.' + filesProperty + '" ng-click="selectFile(file, $event)" ng-class="{ selected: isSelected(file) }">' +
						'<span class="tree-item-name"><i ng-class="getFileIconClass(file)"></i> {{ file.' + displayProperty + ' }}</span>' +
						'</a>' +
						'ss{{folder}}aa';
					element.html('').append($compile(template)(scope));
				}

				render();
			}
		};
	}]);
})(angular);
