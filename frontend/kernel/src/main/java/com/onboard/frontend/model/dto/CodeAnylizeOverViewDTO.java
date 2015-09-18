package com.onboard.frontend.model.dto;

import java.util.Date;

public class CodeAnylizeOverViewDTO {

    private Date updateTime;

    private String branch;

    private String folder;

    private String lastCommitId;

    private Integer lineOfCodeForAnylizeLanguage;

    private String language;

    private Integer fileCount;

    private Integer folderCount;

    private Integer totalLineOfCode;

    private Integer functionCount;

    private Integer classCount;

    private Integer statementCount;

    private Integer accessorCount;

    private String sqaleRating;

    private Double technicalDebtRatio;

    private Integer debt;

    private Integer issuesCount;

    private Integer blockCount;

    private Integer criticalCount;

    private Integer magorCount;

    private Integer minorCount;

    private Integer infoCount;

    private Double duplicationRate;

    private Integer duplicateLineOfCode;

    private Integer duplicateBlockCount;

    private Integer duplicateFileCount;

    private Integer complexity;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getLastCommitId() {
        return lastCommitId;
    }

    public void setLastCommitId(String lastCommitId) {
        this.lastCommitId = lastCommitId;
    }

    public Integer getLineOfCodeForAnylizeLanguage() {
        return lineOfCodeForAnylizeLanguage;
    }

    public void setLineOfCodeForAnylizeLanguage(Integer lineOfCodeForAnylizeLanguage) {
        this.lineOfCodeForAnylizeLanguage = lineOfCodeForAnylizeLanguage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Integer getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(Integer folderCount) {
        this.folderCount = folderCount;
    }

    public Integer getTotalLineOfCode() {
        return totalLineOfCode;
    }

    public void setTotalLineOfCode(Integer totalLineOfCode) {
        this.totalLineOfCode = totalLineOfCode;
    }

    public Integer getFunctionCount() {
        return functionCount;
    }

    public void setFunctionCount(Integer functionCount) {
        this.functionCount = functionCount;
    }

    public Integer getClassCount() {
        return classCount;
    }

    public void setClassCount(Integer classCount) {
        this.classCount = classCount;
    }

    public Integer getStatementCount() {
        return statementCount;
    }

    public void setStatementCount(Integer statementCount) {
        this.statementCount = statementCount;
    }

    public Integer getAccessorCount() {
        return accessorCount;
    }

    public void setAccessorCount(Integer accessorCount) {
        this.accessorCount = accessorCount;
    }

    public String getSqaleRating() {
        return sqaleRating;
    }

    public void setSqaleRating(String sqaleRating) {
        this.sqaleRating = sqaleRating;
    }

    public Double getTechnicalDebtRatio() {
        return technicalDebtRatio;
    }

    public void setTechnicalDebtRatio(Double technicalDebtRatio) {
        this.technicalDebtRatio = technicalDebtRatio;
    }

    public Integer getDebt() {
        return debt;
    }

    public void setDebt(Integer debt) {
        this.debt = debt;
    }

    public Integer getIssuesCount() {
        return issuesCount;
    }

    public void setIssuesCount(Integer issuesCount) {
        this.issuesCount = issuesCount;
    }

    public Integer getBlockCount() {
        return blockCount;
    }

    public void setBlockCount(Integer blockCount) {
        this.blockCount = blockCount;
    }

    public Integer getCriticalCount() {
        return criticalCount;
    }

    public void setCriticalCount(Integer criticalCount) {
        this.criticalCount = criticalCount;
    }

    public Integer getMagorCount() {
        return magorCount;
    }

    public void setMagorCount(Integer magorCount) {
        this.magorCount = magorCount;
    }

    public Integer getMinorCount() {
        return minorCount;
    }

    public void setMinorCount(Integer minorCount) {
        this.minorCount = minorCount;
    }

    public Integer getInfoCount() {
        return infoCount;
    }

    public void setInfoCount(Integer infoCount) {
        this.infoCount = infoCount;
    }

    public Double getDuplicationRate() {
        return duplicationRate;
    }

    public void setDuplicationRate(Double duplicationRate) {
        this.duplicationRate = duplicationRate;
    }

    public Integer getDuplicateLineOfCode() {
        return duplicateLineOfCode;
    }

    public void setDuplicateLineOfCode(Integer duplicateLineOfCode) {
        this.duplicateLineOfCode = duplicateLineOfCode;
    }

    public Integer getDuplicateBlockCount() {
        return duplicateBlockCount;
    }

    public void setDuplicateBlockCount(Integer duplicateBlockCount) {
        this.duplicateBlockCount = duplicateBlockCount;
    }

    public Integer getDuplicateFileCount() {
        return duplicateFileCount;
    }

    public void setDuplicateFileCount(Integer duplicateFileCount) {
        this.duplicateFileCount = duplicateFileCount;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Double getFunctionComplexity() {
        return functionComplexity;
    }

    public void setFunctionComplexity(Double functionComplexity) {
        this.functionComplexity = functionComplexity;
    }

    public Double getClassComplexity() {
        return classComplexity;
    }

    public void setClassComplexity(Double classComplexity) {
        this.classComplexity = classComplexity;
    }

    public Double getFileCOmplexity() {
        return fileCOmplexity;
    }

    public void setFileCOmplexity(Double fileCOmplexity) {
        this.fileCOmplexity = fileCOmplexity;
    }

    private Double functionComplexity;

    private Double classComplexity;

    private Double fileCOmplexity;

}
