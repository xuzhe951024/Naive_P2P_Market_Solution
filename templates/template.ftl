version: "3.3"
services:
<#list modelList as model>
    ${model.serviceName}:
        build: .
        volumes:
            - ${model.workingDir}:/app
        working_dir:
            /app
        container_name: ${model.serviceName}
    </#list>