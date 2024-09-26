pipeline {
    agent any
    environment {
        SPRING_DATASOURCE_URL = credentials('datasource-url')      
        SPRING_DATASOURCE_USERNAME = credentials('datasource-user')
        SPRING_DATASOURCE_PASSWORD = credentials('datasource-pass') 

        appUser = credentials('historyeducation-user')          // Credential cho username
        appDeploy = credentials('historyeducation-deploy')          // Credential cho deploy name
        appVersion = credentials('historyeducation-version')        // Credential cho version
        appName = "history-education"                                // Biến không nhạy cảm
        appType = "jar"                                            // Biến không nhạy cảm
        processName = "${appName}-${appVersion}.${appType}"      // Tạo process name
        folderDeploy = "/deploys/${appDeploy}"                    // Thư mục deploy
        buildScript = "mvn clean install -DskipTests=true"        // Script build
        copyScript = "sudo cp target/${processName} ${folderDeploy}" // Script copy
        killScript = "kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')" // Script kill
        pro_properties = "-Dspring.profiles.active=pro"         // Biến cho profile
        permsScript = "sudo chown -R ${appUser}. ${folderDeploy}" // Script đổi quyền
        runScript = """sudo su ${appUser} -c "cd ${folderDeploy}; java -jar ${pro_properties} ${processName} > nohup.out 2>&1 &" """ // Script chạy ứng dụng
    }
    stages {
        stage('info') {
            steps {
                sh(script: """ whoami; pwd; ls -la; """) // Xem thông tin người dùng
            }
        }
        stage('build') {
            steps {
                sh(script: """ ${buildScript} """) // Thực hiện build
            }
        }
        stage('deploy') {
            steps {
                script {
                    // Tạo biến tạm cho tên file
                    def fileToCopy = "target/${processName}"
                    sh(script: "sudo cp ${fileToCopy} ${folderDeploy}") // Thực hiện copy
                    sh(script: """ ${permsScript} """) // Thực hiện đổi quyền
                    sh(script: """ whoami; """) // Kiểm tra người dùng
                    sh(script: """ ${runScript} """) // Chạy ứng dụng
                }
            }
        }
    }
}