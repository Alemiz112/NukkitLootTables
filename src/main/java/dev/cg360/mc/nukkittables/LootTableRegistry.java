package dev.cg360.mc.nukkittables;

import cn.nukkit.Server;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.cg360.mc.nukkittables.conditions.*;
import dev.cg360.mc.nukkittables.executors.TableConditionExecutor;
import dev.cg360.mc.nukkittables.executors.TableFunctionExecutor;
import dev.cg360.mc.nukkittables.functions.FunctionExecutorSetCount;
import dev.cg360.mc.nukkittables.functions.FunctionExecutorSetMeta;
import dev.cg360.mc.nukkittables.types.LootTable;
import dev.cg360.mc.nukkittables.types.entry.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class LootTableRegistry {

    private static final LootTableRegistry INSTANCE = new LootTableRegistry();
    public static final Class<? extends TableEntry> FALLBACK_ENTRY = TableEntryEmpty.class;

    protected HashMap<String, TableConditionExecutor<?>> conditionExecutors;
    protected HashMap<String, TableFunctionExecutor> functionExecutors;
    protected HashMap<String, Class<? extends TableEntry>> entryTypes;

    protected HashMap<String, LootTable> lootTables;

    private LootTableRegistry() {
        this.conditionExecutors = new HashMap<>();
        this.functionExecutors = new HashMap<>();
        this.entryTypes = new HashMap<>();

        this.lootTables = new HashMap<>();

        this.registerDefaultTypes();
        this.registerDefaultConditions();
        this.registerDefaultFunctions();
    }

    public void registerDefaultConditions() {
        // minecraft namespace
        this.registerConditionExecutor(new ConditionExecutorAlternative());
        this.registerConditionExecutor(new ConditionExecutorInverted());
        this.registerConditionExecutor(new ConditionExecutorRandomChance());
        this.registerConditionExecutor(new ConditionExecutorTimeCheck());
        this.registerConditionExecutor(new ConditionExecutorWeatherCheck());

        // nukkit namespace
        this.registerConditionExecutor(new ConditionExecutorPluginEnabled());
    }

    public void registerDefaultFunctions() {
        // minecraft namespace
        this.registerFunctionExecutor(new FunctionExecutorSetCount());

        // nukkit namespace
        this.registerFunctionExecutor(new FunctionExecutorSetMeta());
    }

    public void registerDefaultTypes() {
        this.registerEntryType("minecraft:item", TableEntryItem.class);
        this.registerEntryType("minecraft:group", TableEntryGroup.class);
        this.registerEntryType("minecraft:alternatives", TableEntryAlternatives.class);
        this.registerEntryType("minecraft:sequence", TableEntrySequence.class);
        this.registerEntryType("minecraft:empty", TableEntryEmpty.class);
    }

    public void loadAllLootTablesFromStorage(String name, boolean includeSubfolders) {
        File root = new File(Server.getInstance().getDataPath() + "loottables/" + name);
        if (root.exists() && root.isDirectory()) {
            try {
                for (File file : root.listFiles()) {
                    if (file.isDirectory() && includeSubfolders) {
                        loadAllLootTablesFromStorage(name + "/" + file.getName(), true);
                    } else {
                        try {
                            registerStoredLootTable(name + "/" + file.getName());
                        } catch (Exception err) {
                            Server.getInstance().getLogger().error("Error loading loot table at: " + file.getAbsolutePath());
                            err.printStackTrace();
                        }
                    }
                }
            } catch (Exception err) {
                Server.getInstance().getLogger().error("Error loading loot tables in: " + root.getAbsolutePath());
                err.printStackTrace();
            }
        }
    }

    public boolean registerStoredLootTable(String name) throws IOException {
        Path path = Paths.get(Server.getInstance().getDataPath(), "loottables/", name);
        String lootName = name.toLowerCase().substring(0, name.length() - 5);
        return this.registerStoredLootTable(path, lootName);
    }

    public boolean registerStoredLootTable(Path path, String name) throws IOException {
        if (!Files.isRegularFile(path)) {
            throw new IllegalStateException("File does not exist: " + path);
        }

        try (InputStream stream = Files.newInputStream(path);
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return this.registerLootTableFromString(name, JsonParser.parseReader(reader));
        }
    }

    public boolean registerLootTableFromString(String name, String jsonData) {
        return this.registerLootTableFromString(name, JsonParser.parseString(jsonData));
    }

    public boolean registerLootTableFromString(String name, JsonElement json) {
        Optional<LootTable> pt = LootTable.createLootTableFromString(json);
        if (pt.isPresent()) {
            lootTables.put(name.toLowerCase().trim().substring((name.startsWith("/") ? 1 : 0)), pt.get());
            return true;
        }
        return false;
    }

    public void registerConditionExecutor(TableConditionExecutor<?> condition) {
        String originalID = condition.getConditionType().toLowerCase();
        conditionExecutors.put(originalID, condition);
        if (originalID.startsWith("minecraft:")) {
            String shortenedID = originalID.substring(10);
            conditionExecutors.put(shortenedID, condition);
        }
    }

    public void registerFunctionExecutor(TableFunctionExecutor function) {
        String originalID = function.getFunctionType().toLowerCase();
        functionExecutors.put(originalID, function);
        if (originalID.startsWith("minecraft:")) {
            String shortenedID = originalID.substring(10);
            functionExecutors.put(shortenedID, function);
        }
    }

    public void registerEntryType(String id, Class<? extends TableEntry> entryType) {
        String originalID = id.toLowerCase();
        entryTypes.put(originalID, entryType);
        if (originalID.startsWith("minecraft:")) {
            String shortenedID = originalID.substring(10);
            entryTypes.put(shortenedID, entryType);
        }
    }

    public TableConditionExecutor<?> getConditionExecutor(String id) {
        return conditionExecutors.get(id.toLowerCase());
    }

    public TableFunctionExecutor getFunctionExecutor(String id) {
        return functionExecutors.get(id.toLowerCase());
    }

    public Class<? extends TableEntry> getEntryTypeClass(String id) {
        return entryTypes.get(id.toLowerCase());
    }

    public LootTable getLootTable(String id) {
        return lootTables.get(id.toLowerCase());
    }

    public List<String> getConditionExecutors() {
        return new ArrayList<>(conditionExecutors.keySet());
    }

    public List<String> getFunctionExecutors() {
        return new ArrayList<>(functionExecutors.keySet());
    }

    public List<String> getEntryTypes() {
        return new ArrayList<>(entryTypes.keySet());
    }

    public List<String> getLootTables() {
        return new ArrayList<>(lootTables.keySet());
    }

    public static LootTableRegistry get() {
        return INSTANCE;
    }
}
