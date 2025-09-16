package galysso.structures_features.util;

import galysso.structures_features.StructuresFeatures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class NetworkUtil {
    public static final Identifier STRUCTURE_WELCOME_PAYLOAD = StructuresFeatures.identifier("structure_welcome_payload");
    public static final Identifier STRUCTURE_FAREWELL_PAYLOAD = StructuresFeatures.identifier("structure_farewell_payload");

    public static void receiveWelcome(MinecraftClient client, String name) {
        if (name == null || name.isEmpty()) return;
        client.inGameHud.setOverlayMessage(
            Text.translatable("structures_features.message_prefix.entering").append(name).formatted(Formatting.DARK_GREEN),
            false
        );
    }

    public static void receiveFarewell(MinecraftClient client, String name) {
        if (name == null || name.isEmpty()) return;
        client.inGameHud.setOverlayMessage(
            Text.translatable("structures_features.message_prefix.leaving").append(name).formatted(Formatting.DARK_RED),
            false
        );
    }
}
