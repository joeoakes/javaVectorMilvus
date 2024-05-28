import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;

public class MilvusConnection {
    private static MilvusClient client;

    public static MilvusClient connect() {
        if (client == null) {
            ConnectParam connectParam = ConnectParam.newBuilder()
                    .withHost("localhost")
                    .withPort(19530)
                    .build();
            client = new MilvusServiceClient(connectParam);
        }
        return client;
    }

    public static void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}
