package functlyser.repository;

import com.arangodb.ArangoCursor;
import com.arangodb.springframework.annotation.Param;
import com.arangodb.springframework.annotation.Query;
import com.arangodb.springframework.annotation.QueryOptions;
import com.arangodb.springframework.repository.ArangoRepository;
import functlyser.model.Data;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

public interface DataRepository extends ArangoRepository<Data> {

    Data findFirstByFileName(String fileName);

    Data findFirstByRawColumnsNotNull();

    Data findFirstByWorkColumnsNotNull();

    @Query("FOR r IN @@col\n" +
            "FILTER r.fileName == @fileName\n" +
            "REMOVE r IN @@col\n" +
            "COLLECT WITH COUNT INTO c\n" +
            "RETURN [c]")
    ArangoCursor<Long> removeByFileName(@Param(value = "fileName") String fileName, @Param(value = "@col") Object collection);
}
