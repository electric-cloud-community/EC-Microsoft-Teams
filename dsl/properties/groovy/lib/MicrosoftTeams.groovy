import com.cloudbees.flowpdf.*
import groovy.json.StringEscapeUtils
import com.cloudbees.flow.examples.plugin.*

/**
* MicrosoftTeams
*/
class MicrosoftTeams extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName     : '@PLUGIN_KEY@',
                pluginVersion  : '@PLUGIN_VERSION@',
                configFields   : ['config'],
                configLocations: ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
    }

/**
    * sendMessage - Send Message/Send Message
    * Add your code into this method and it will be called when the step runs
    * @param config (required: true)
    * @param webhookUrl (required: true)
    
    */
    def sendMessage(StepParameters p, StepResult sr) {
        Context context = getContext()
        def config = context.getConfigValues()

        def webhookUrl = p.getRequiredParameter('webhookUrl').getValue()
        String message = p.getRequiredParameter('message').getValue()

        TeamsMessageSender sender = new TeamsMessageSender(webhookUrl)

        // Add Proxy configuration if specified at Plugin Configuration level
        if (config.getRequiredParameter('proxyHost').getValue()?.trim()) {
            def credential = config.getRequiredCredential('credential')
            sender.addProxy(config.getRequiredParameter('proxyHost').getValue(),
                    config.getRequiredParameter('proxyPort').getValue(),
                    credential.getUserName(),
                    credential.getSecretValue())
        }

        sender.sendMessage('{"text": "' + StringEscapeUtils.escapeJava(message) + '", "TextFormat":"markdown"}')

        sr.apply()
    }

// === step ends ===

}