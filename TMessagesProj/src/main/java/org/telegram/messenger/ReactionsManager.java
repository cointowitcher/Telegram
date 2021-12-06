package org.telegram.messenger;

import android.util.Pair;

import com.google.android.exoplayer2.util.Log;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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
    private long lastDate = 0;

    private int hash = -1;

    public ArrayList<TLRPC.TL_availableReaction> availableReactions;
    public HashMap<String, TLRPC.TL_availableReaction> availableReactionHashMap;

    public ReactionsManager(int currentAccount) {
        super();
        this.currentAccount = currentAccount;
        this.accountInstance = AccountInstance.getInstance(currentAccount);
        this.availableReactions = new ArrayList<>();
        this.availableReactionHashMap = new HashMap<>();
    }

    public void loadReactions() {
        if (lastDate != 0 && Calendar.getInstance().getTime().getTime() - lastDate > 3600 * 1000) {
            return;
        }
        TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
        if (hash != -1) {
            req.hash = hash;
        }
        getConnectionsManager().sendRequest(req, (response, error) -> {
            if (response == null) { return; }
            if (response instanceof TLRPC.TL_messages_availableReactionsNotModified) {
            }
            lastDate = Calendar.getInstance().getTime().getTime();
            if (response instanceof TLRPC.TL_messages_availableReactions) {
                TLRPC.TL_messages_availableReactions resp = (TLRPC.TL_messages_availableReactions)response;
                this.hash = resp.hash;
                availableReactions = resp.reactions;
                for(int i = 0; i < availableReactions.size(); i++) {
                    TLRPC.TL_availableReaction availableReactions = this.availableReactions.get(i);
                    availableReactionHashMap.put(availableReactions.reaction, availableReactions);
                }
            }
        });
    }

    public TLRPC.TL_availableReaction getAvailableReaction(String reaction) {
        return availableReactionHashMap.get(reaction);
    }

    public void removeReaction(MessageObject messageObject, String reaction, ChatActivity parent, SendReactionResponse response) {
        getSendMessagesHelper().removeReaction(messageObject, reaction, parent, (pair) -> {
            Pair<TLObject, TLRPC.TL_error> obj = (Pair<TLObject, TLRPC.TL_error>) pair;
            response.didSend(obj.first != null);
        });
    }

    public void sendReaction(MessageObject messageObject, String reaction, ChatActivity parent, SendReactionResponse response) {
        getSendMessagesHelper().sendReaction(messageObject, reaction, parent, (pair) -> {
            Pair<TLObject, TLRPC.TL_error> obj = (Pair<TLObject, TLRPC.TL_error>) pair;
            response.didSend(obj.first != null);
        });
    }

    public void getMessagesReactions(long chatId, ArrayList<Integer> ids) {
        TLRPC.TL_messages_getMessagesReactions req = new TLRPC.TL_messages_getMessagesReactions();
        req.peer = getMessagesController().getInputPeer(chatId);
        req.id = ids;
        getConnectionsManager().sendRequest(req, (response, error) -> {
            TLRPC.TL_updates updates = (TLRPC.TL_updates) response;
            getMessagesController().processUpdateArray(updates.updates, new ArrayList<>(), new ArrayList<>(), false, updates.date);
        });
    }

    // MARK: - Helper
    public TLRPC.TL_availableReaction getAvailableReactionFor(String reaction) {
        return getAvailableReaction(reaction);
    }

    public void locallyUpdateMessageReactions(long dialogId, int msgId, TLRPC.TL_messageReactions reactions) {
        getMessagesStorage().updateMessageReactions(dialogId, msgId, reactions);
    }
    public void locallyUpdateMessageReactions(TLRPC.Message message) {
        locallyUpdateMessageReactions(MessageObject.getPeerId(message.peer_id), message.id, message.reactions);
    }

    private ConnectionsManager getConnectionsManager() { return accountInstance.getConnectionsManager(); }
    private MessagesController getMessagesController() { return accountInstance.getMessagesController(); }
    private MessagesStorage getMessagesStorage() { return accountInstance.getMessagesStorage(); }
    private SendMessagesHelper getSendMessagesHelper() { return accountInstance.getSendMessagesHelper(); }

    @FunctionalInterface
    public interface SendReactionResponse {
        void didSend(boolean successful);
    }
}
