package com.github.flandre923.berrypouch;

import com.github.flandre923.berrypouch.network.ModNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCommon {
    public static final String MOD_ID = "berrypouch";
    public static final String MOD_NAME = "Berry Pouch";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        ModRegistries.init();
        ModNetworking.register();
    }

    public static void registerCommands() {
        // 由平台特定代码调用
    }
}
