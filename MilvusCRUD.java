import io.milvus.client.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.dml.*;
import io.milvus.grpc.*;
import java.util.*;

//https://milvus.io/blog/2021-11-19-run-milvus-2.0-on-windows.md#Install-from-the-binary-package

public class MilvusCRUD {

    private MilvusClient client;

    public MilvusCRUD() {
        client = MilvusConnection.connect();
    }

    public void createCollection(String collectionName) {
        FieldType idField = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();

        FieldType vectorField = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(128)
                .build();

        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .withDescription("Test collection")
                .addFieldType(idField)
                .addFieldType(vectorField)
                .build();

        client.createCollection(createCollectionParam);
        System.out.println("Collection created: " + collectionName);
    }

    public void insertData(String collectionName, List<List<Float>> vectors) {
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("vector", vectors));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(collectionName)
                .withFields(fields)
                .build();

        client.insert(insertParam);
        System.out.println("Data inserted into collection: " + collectionName);
    }

    public void searchData(String collectionName, List<List<Float>> queryVectors) {
        List<String> fieldNames = Collections.singletonList("vector");

        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withMetricType(MetricType.L2)
                .withTopK(10)
                .withVectors(queryVectors)
                .withVectorFieldName("vector")
                .withOutFields(fieldNames)
                .withParams("{\"nprobe\": 10}")
                .build();

        R<SearchResults> searchResults = client.search(searchParam);
        /*if (searchResults != null && searchResults.getStatus() == R.Status.SUCCESS) {
            SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResults.getData().getResults());

            for (int i = 0; i < wrapper.getFieldData("id").size(); ++i) {
                System.out.println("Search result id: " + wrapper.getFieldData("id").get(i));
            }
        } else {
            System.err.println("Search failed: " + searchResults.getMessage());
        }
        */
    }

    public void deleteCollection(String collectionName) {
        DropCollectionParam dropCollectionParam = DropCollectionParam.newBuilder()
                .withCollectionName(collectionName)
                .build();

        client.dropCollection(dropCollectionParam);
        System.out.println("Collection deleted: " + collectionName);
    }

    public void close() {
        MilvusConnection.disconnect();
    }

    public static void main(String[] args) {
        MilvusCRUD crud = new MilvusCRUD();

        String collectionName = "example_collection";
        crud.createCollection(collectionName);

        List<List<Float>> vectors = new ArrayList<>();
        vectors.add(Arrays.asList(new Float[128]));  // Add your 128-dimensional vectors here
        crud.insertData(collectionName, vectors);

        List<List<Float>> queryVectors = new ArrayList<>();
        queryVectors.add(Arrays.asList(new Float[128]));  // Add your 128-dimensional query vectors here
        crud.searchData(collectionName, queryVectors);

        crud.deleteCollection(collectionName);
        crud.close();
    }
}
