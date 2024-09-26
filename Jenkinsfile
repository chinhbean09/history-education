pipeline {
    agent any
    environment {
        SPRING_DATASOURCE_URL = credentials('datasource-url')      
        SPRING_DATASOURCE_USERNAME = credentials('datasource-user')
        SPRING_DATASOURCE_PASSWORD = credentials('datasource-pass') 

        appUser = credentials('historyeducation-user')          
        appDeploy = credentials('historyeducation-deploy')          
        appVersion = credentials('historyeducation-version')        
        appName = "history-education"                                
        appType = "jar"                                            
        processName = "${appName}-${appVersion}.${appType}"      
        folderDeploy = "/deploys/${appDeploy}"                    
        buildScript = "mvn clean install -DskipTests=true"        
        copyScript = "sudo cp target/${processName} ${folderDeploy}" 
        killScript = "kill -9 \$(ps -ef| grep ${processName}| grep -v grep| awk '{print \$2}')" 
        pro_properties = "-Dspring.profiles.active=pro"         
        permsScript = "sudo chown -R ${appUser}. ${folderDeploy}" 
        runScript = """sudo su ${appUser} -c "cd ${folderDeploy}; java -jar ${pro_properties} ${processName} > nohup.out 2>&1 &" """ 
    }
    stages {
        stage('info') {
            steps {
                sh(script: """ whoami; pwd; ls -la; """)
            }
        }
        stage('build') {
            steps {
                sh(script: """ ${buildScript} """)
            }
        }
        stage('kill') {
            steps {
                script {
                    def processId = sh(script: "ps -ef | grep ${processName} | grep -v grep | awk '{print \$2}'", returnStdout: true).trim()
                    if (processId) {
                        echo "Killing process ${processId}"
                        sh(script: "sudo kill -9 ${processId}")
                    } else {
                        echo "No running process found for ${processName}"
                    }
                }
            }
        }

        stage('deploy') {
            steps {
                script {
                    def fileToCopy = "target/${processName}"
                    sh(script: "sudo cp ${fileToCopy} ${folderDeploy}")
                    sh(script: """ ${permsScript} """)
                    sh(script: """ whoami; """)
                    sh(script: """ ${runScript} """)
                }
            }
        }
    }
}
