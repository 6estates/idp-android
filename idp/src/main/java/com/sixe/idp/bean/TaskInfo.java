package com.sixe.idp.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * submit task information class
 */
public class TaskInfo {
    /**
     * file type,CBKS,INV etc
     */
    private String fileType;
    /**
     * absolute path of PDF
     */
    private String filePath;
    /**
     * absolute path of images
     */
    private List<String> imagePaths;
    /**
     * false or true
     */
    private boolean hitl;

    public TaskInfo(String fileType, String filePath, List<String> imagePaths, boolean hitl) {
        this.fileType = fileType;
        this.filePath = filePath;
        this.imagePaths = imagePaths;
        this.hitl = hitl;
    }

    public String fileType(){
        return fileType;
    }

    public String filePath(){
        return filePath;
    }

    public List<String> imagePaths(){
        return imagePaths;
    }

    public boolean hitl(){
        return hitl;
    }

    public static final class Builder {
        private String fileType ="CBKS";
        private String filePath;
        private List<String> imagePaths = new ArrayList<>();
        private boolean hitl = false;
        public Builder(){}

        public TaskInfo build() {
            return new TaskInfo(fileType,filePath,imagePaths,hitl);
        }

        public Builder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder imagePaths(List<String> imagePaths) {
            this.imagePaths = imagePaths;
            return this;
        }

        public Builder hitl(boolean hitl) {
            this.hitl = hitl;
            return this;
        }
    }
}
