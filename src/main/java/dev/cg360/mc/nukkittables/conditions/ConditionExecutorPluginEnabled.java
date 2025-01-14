package dev.cg360.mc.nukkittables.conditions;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.cg360.mc.nukkittables.context.RollContext;
import dev.cg360.mc.nukkittables.executors.TableConditionExecutor;

public class ConditionExecutorPluginEnabled extends TableConditionExecutor<RollContext> {

    @Override
    public boolean isConditionPassed0(RollContext context, JsonObject data) {
        JsonElement pluginElement = data.get("name");
        JsonElement versionElement = data.get("version");

        if(pluginElement instanceof JsonPrimitive){
            JsonPrimitive pluginPrimitive = (JsonPrimitive) pluginElement;

            if(pluginPrimitive.isString()){
                String pluginID = pluginPrimitive.getAsString();
                Plugin plugin = Server.getInstance().getPluginManager().getPlugin(pluginID);

                if(plugin != null && plugin.isEnabled()){
                    if(versionElement instanceof JsonPrimitive){
                        JsonPrimitive versionPrimitive = (JsonPrimitive) versionElement;

                        if(versionPrimitive.isNumber() || versionPrimitive.isString()){
                            String versionString = versionPrimitive.getAsString().toLowerCase().trim();
                            String nukkitString = plugin.getDescription().getVersion().toLowerCase().trim();
                            return versionString.equals(nukkitString);
                        }
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getConditionType() {
        return "nukkit:plugin_enabled";
    }
}
