import io.milvus.client.MilvusClient;
import io.milvus.client.MilvusClient.Builder;
import io.milvus.client.MilvusClientFactory;
import io.milvus.grpc.*;

public class MilvusConnection {
    private static MilvusClient client;

    public static MilvusClient connect() {
        if (client == null) {
            Builder builder = new MilvusClient.Builder()
                    .withHost("localhost")
                    .withPort(19530);
            client = MilvusClientFactory.newClient(builder);
        }
        return client;
    }

    public static void disconnect() {
        if (client != null) {
            client.close();
        }
    }
}
