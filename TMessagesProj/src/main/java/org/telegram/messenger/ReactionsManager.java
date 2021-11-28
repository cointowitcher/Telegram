package org.telegram.messenger;

import com.google.android.exoplayer2.util.Log;

import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;

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

    public void doSomething() {
//        TLRPC.TL_messages_getAvailableReactions req = new TLRPC.TL_messages_getAvailableReactions();
//        accountInstance.getConnectionsManager().sendRequest(req, (response, error) -> {
//            if (error == null) {
////                ArrayList<SerializedData> cache = new ArrayList<>();
////                TLRPC.TL_messages_availableReactions resp = (TLRPC.TL_messages_availableReactions)response;
////                for(int i = 0; i < resp.reactions.size(); i++) {
////                    SerializedData data = new SerializedData();
////                    TLRPC.TL_availableReaction rec = resp.reactions.get(i);
////                    rec.serializeToStream(data);
////
////                    cache.add(data);
////                }
//                Log.e("serg", "w");
//            }
//        });
    }

    // MARK: - Helper
    private ConnectionsManager getConnectionsManager() {
        return accountInstance.getConnectionsManager();
    }
}
