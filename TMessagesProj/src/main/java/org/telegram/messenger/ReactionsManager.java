package org.telegram.messenger;

import android.util.Pair;

import com.google.android.exoplayer2.util.Log;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

import java.util.ArrayList;

public class ReactionsManager {
    private static volatile ReactionsManager[] Instance = new ReactionsManager[UserConfig.MAX_ACCOUNT_COUNT];
    public static ReactionsManager getInstance(int num) {
        ReactionsManager localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new ReactionsManager(num);
                }
            }
        }
        return localInstance;
    }

    private int currentAccount;
    private AccountInstance accountInstance;

    public ArrayList<TLRPC.TL_availableReaction> availableReactions;

    public ReactionsManager(int currentAccount) {
        super();
        this.currentAccount = currentAccount;
        this.accountInstance = AccountInstance.getInstance(currentAccount);
        this.availableReactions = new ArrayList<>();
    }

    public void loadReactions() {
        TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
        getConnectionsManager().sendRequest(req, (response, error) -> {
            if (error == null) {
                TLRPC.TL_messages_availableReactions resp = (TLRPC.TL_messages_availableReactions)response;
                availableReactions = resp.reactions;
            }
        });
    }

    public void sendReaction(MessageObject messageObject, String reaction, ChatActivity parent, SendReactionResponse response) {
        accountInstance.getSendMessagesHelper().sendReaction(messageObject, reaction, parent, (pair) -> {
            Pair<TLObject, TLRPC.TL_error> obj = (Pair<TLObject, TLRPC.TL_error>) pair;
            response.didSend(obj.first != null);
        });
    }

    // MARK: Configuring MessageObject


    // MARK: - Helper
    private ConnectionsManager getConnectionsManager() {
        return accountInstance.getConnectionsManager();
    }

    @FunctionalInterface
    public interface SendReactionResponse {
        void didSend(boolean successful);
    }
}
