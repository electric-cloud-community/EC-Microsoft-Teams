pipeline {
  options {
    buildDiscarder(logRotator(numToKeepStr: '10'))
  }
  agent {
    kubernetes {
      yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: some-label-value
spec:
  containers:
  - name: gradle
    image: gradle:6.5.0-jdk8
    command:
    - cat
    tty: true
  - name: flowpdk
    image: nexus.cloudbees.guru:5556/flowpdk:edge
    command:
    - cat
    tty: true
  imagePullSecrets:
    - name: regcred
"""
    }
  }
  environment {
    SONAR_TOKEN = credentials('sonar.login')
    NEXUS_VERSION = "nexus3"
    NEXUS_PROTOCOL = "https"
    NEXUS_URL = "nexus.cloudbees.guru:8081"
    NEXUS_REPOSITORY = "shared-demos"
    NEXUS_CREDENTIAL_ID = "nexus"
    ROLLOUT_APP_TOKEN = "$ROLLOUT_APP_TOKEN"
    ROLLOUT_USER_TOKEN = "$ROLLOUT_USER_TOKEN"
    }
  stages {
    stage('Gradle: copy dependencies') {
      steps {
          git(url:'https://github.com/cloudbees-guru/EC-Microsoft-Teams', credentialsId: 'github-cloudbees-guru')
          container('gradle') {
            withMaven(
                      mavenSettingsConfig: '4123d3ce-22c2-477d-83d7-623049473250') {
              sh 'gradle copyDependencies'
            }
          }
      }
    }
    stage('Flowpdk: build the plugin') {
      steps {
          container('flowpdk') {
            sh 'flowpdk build'
          }
      }
    }
    stage('Publish to Nexus') {
      steps {
          container('gradle') {
            script {
              // Extract the path from the File found
              artifactPath = 'build/EC-Microsoft-Teams.zip';
              // Assign to a boolean response verifying If the artifact name exists
              artifactExists = fileExists artifactPath;

              if(artifactExists) {
                nexusArtifactUploader(
                  nexusVersion: NEXUS_VERSION,
                  protocol: NEXUS_PROTOCOL,
                  nexusUrl: NEXUS_URL,
                  groupId: 'com.cloudbees.flow.plugin',
                  version: '1.0.0',
                  repository: NEXUS_REPOSITORY,
                  credentialsId: NEXUS_CREDENTIAL_ID,
                  artifacts: [
                    // Artifact generated such as .jar, .ear and .war files.
                    [artifactId: 'EC-Microsoft-Teams',
                      classifier: '',
                      file: artifactPath,
                      type: 'zip']
                    ]
                  );
               } else {
                 error "*** File: ${artifactPath}, could not be found";
               }
            }
          }
      }
    }
  }

}