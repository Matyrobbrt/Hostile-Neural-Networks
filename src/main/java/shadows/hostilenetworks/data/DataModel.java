package shadows.hostilenetworks.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import shadows.hostilenetworks.HostileNetworks;

public class DataModel {

	protected ResourceLocation id;
	protected final EntityType<?> type;
	protected final TranslationTextComponent name;
	protected final float guiScale;
	protected final float guiXOff, guiYOff;
	protected final int simCost;
	protected final ItemStack baseDrop;
	protected final ItemStack pristineDrop;
	protected final List<TranslationTextComponent> trivia;

	public DataModel(EntityType<?> type, TranslationTextComponent name, float guiScale, float guiXOff, float guiYOff, int simCost, ItemStack baseDrop, ItemStack pristineDrop, List<TranslationTextComponent> trivia) {
		this.type = type;
		this.name = name;
		this.guiScale = guiScale;
		this.guiYOff = guiYOff;
		this.guiXOff = guiXOff;
		this.simCost = simCost;
		this.baseDrop = baseDrop;
		this.pristineDrop = pristineDrop;
		this.trivia = trivia;
	}

	public void setId(ResourceLocation id) {
		if (this.id != null) throw new UnsupportedOperationException("Attempted to change the already set ID of a DataModel!");
		this.id = id;
	}

	public ResourceLocation getId() {
		return id;
	}

	public ITextComponent getName() {
		return this.name;
	}

	public List<TranslationTextComponent> getTrivia() {
		return trivia;
	}

	public float getScale() {
		return guiScale;
	}

	public int getSimCost() {
		return this.simCost;
	}

	public EntityType<?> getType() {
		return this.type;
	}

	public static class Adapter implements JsonDeserializer<DataModel>, JsonSerializer<DataModel> {

		public static final DataModel.Adapter INSTANCE = new Adapter();

		@Override
		public JsonElement serialize(DataModel src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.addProperty("type", src.type.getRegistryName().toString());
			obj.addProperty("name", src.name.getKey());
			obj.addProperty("gui_scale", src.guiScale);
			obj.addProperty("gui_x_offset", src.guiXOff);
			obj.addProperty("gui_y_offset", src.guiYOff);
			obj.addProperty("sim_cost", src.simCost);
			obj.add("base_drop", context.serialize(src.baseDrop));
			obj.add("pristine_drop", context.serialize(src.pristineDrop));
			JsonArray arr = new JsonArray();
			src.trivia.forEach(t -> arr.add(t.getKey()));
			obj.add("trivia", arr);
			return obj;
		}

		@Override
		public DataModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = json.getAsJsonObject();
			EntityType<?> t = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(obj.get("type").getAsString()));
			if (t == null) throw new JsonParseException("DataModel has invalid entity type " + obj.get("type").getAsString());
			TranslationTextComponent name = new TranslationTextComponent(obj.get("name").getAsString());
			if (obj.has("name_color")) name.withStyle(Style.EMPTY.withColor(Color.fromRgb(Integer.decode(obj.get("name_color").getAsString()))));
			float guiScale = obj.get("gui_scale").getAsFloat();
			float guiXOff = obj.get("gui_x_offset").getAsFloat();
			float guiYOff = obj.get("gui_y_offset").getAsFloat();
			int simCost = obj.get("sim_cost").getAsInt();
			ItemStack baseDrop = context.deserialize(obj.get("base_drop"), ItemStack.class);
			ItemStack pristineDrop = context.deserialize(obj.get("pristine_drop"), ItemStack.class);
			List<TranslationTextComponent> trivia = new ArrayList<>();
			if (obj.has("trivia")) {
				JsonArray arr = obj.get("trivia").getAsJsonArray();
				for (int i = 0; i < Math.min(4, arr.size()); i++) {
					trivia.add(new TranslationTextComponent(arr.get(i).getAsString()));
				}
				if (arr.size() > 4) HostileNetworks.LOGGER.error("Data Model for " + t.getRegistryName() + " has more than the max allowed trivia lines (4).");
			}
			return new DataModel(t, name, guiScale, guiXOff, guiYOff, simCost, baseDrop, pristineDrop, trivia);
		}

	}

}