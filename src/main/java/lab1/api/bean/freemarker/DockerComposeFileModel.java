package lab1.api.bean.freemarker;

/**
 * @project: CS677_Lab1
 * @description:
 * @author: zhexu
 * @create: 10/30/22
 **/
public class DockerComposeFileModel {
    private String serviceName;
    private String workingDir;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
