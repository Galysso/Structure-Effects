package galysso.structures_features.util;

import galysso.structures_features.StructuresFeatures;
import galysso.structures_features.network.StructureNamePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class NetworkUtil {
    public static final Identifier STRUCTURE_NAME_PAYLOAD = StructuresFeatures.identifier("structure_name_payload");

    public static void init() {
        PayloadTypeRegistry.playS2C().register(StructureNamePayload.ID, StructureNamePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(StructureNamePayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                ClientPlayerEntity player = client.player;
                if (player == null) return;

                boolean entering = payload.entering();
                String name = payload.structureName();

                // 1) Chat
                //player.sendMessage(Text.literal("Structure: " + name));

                // 2) Overlay (au-dessus de la hotbar, style “action bar”)
                if (entering) {
                    client.inGameHud.setOverlayMessage(
                        Text.translatable("structures_features.message_prefix.entering").append(name).formatted(Formatting.DARK_GREEN),
                        false
                    );
                } else {
                    client.inGameHud.setOverlayMessage(
                        Text.translatable("structures_features.message_prefix.leaving").append(name).formatted(Formatting.DARK_RED),
                        false
                    );
                }

                // 3) Toast (notification en haut à droite)
                /*SystemToast.add(
                        client.getToastManager(),
                        SystemToast.Type.TUTORIAL_HINT,
                        Text.literal("Structure détectée"),
                        Text.literal(name)
                );*/
            });
        });
    }
}
