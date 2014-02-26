package org.wso2.balana.ctx;

public class ResourceActionMapping {

    private String resourceName;
    private String actionName;
    private String envName;

    public ResourceActionMapping() {

    }

    public ResourceActionMapping(String resourceName, String actionName, String envName) {
        super();
        this.resourceName = resourceName;
        this.actionName = actionName;
        this.envName = envName;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

}
