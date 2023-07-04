package shadows.hostilenetworks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;

public class FixStuff {
    public static void main(String[] args) throws Exception {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final Path path = Path.of("P:\\Minecraft\\HostileNeuralNetworks\\Common\\src\\main\\resources\\data\\hostilenetworks\\data_models");
        try (final var str = Files.walk(path, Integer.MAX_VALUE).filter(p -> p.toString().endsWith(".json"))) {
            final var itr = str.iterator();
            while (itr.hasNext()) {
                final Path next = itr.next();
                final JsonObject json = gson.fromJson(Files.readString(next), JsonObject.class);
                if (json.has("conditions")) {
                    final String modId = json.getAsJsonArray("conditions").get(0).getAsJsonObject().get("modid").getAsString();

                    json.remove("conditions");
                    var arr = new JsonArray();
                    var cond = new JsonObject();
                    cond.addProperty("type", "forge:mod_loaded");
                    cond.addProperty("modid", modId);
                    arr.add(cond);
                    json.add("forge:conditions", arr);

                    arr = new JsonArray();
                    cond = new JsonObject();
                    cond.addProperty("condition", "fabric:all_mods_loaded");
                    var modsArray = new JsonArray(); modsArray.add(modId);
                    cond.add("values", modsArray);
                    arr.add(cond);
                    json.add("fabric:load_conditions", arr);

                    Files.writeString(next, gson.toJson(json));
                }
            }
        }
    }
}
