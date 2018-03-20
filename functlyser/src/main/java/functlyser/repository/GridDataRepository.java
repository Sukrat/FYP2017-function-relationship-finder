package functlyser.repository;

import com.arangodb.springframework.repository.ArangoRepository;
import functlyser.model.GridData;

public interface GridDataRepository extends ArangoRepository<GridData> {
}
