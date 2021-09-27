package br.com.dynamic.dependencies

class Dependencies{
    def call (jenkins) {
        jenkins.podTemplate(
            containers: [
                jenkins.containerTemplate(name: 'java', image: jenkins.env.CI_IMAGE, ttyEnabled: true, command: 'cat')
            ],
            yamlMergeStrategy: jenkins.merge(),
            workspaceVolume: jenkins.persistentVolumeClaimWorkspaceVolume(
                claimName: "pvc-${jenkins.env.JENKINS_AGENT_NAME}",
                readOnly: false
            )
        )

        {
            jenkins.node(jenkins.POD_LABEL){
                jenkins.container('java'){
                    jenkins.sh label: "Installing dependencies", script: "gradle dependencies"
                    jenkins.env.APP_VERSION = jenkins.sh(label: 'Recuperando Versão da Aplicação...',
                        script: "cat build.gradle* | grep 'version =' | awk '{print \$3}'",
                        returnStdout: true).replace("\"", "").trim()
                }
            }
        }
    }
}