package run.halo.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlatformRepoResponse {
    private String platformName;
    private ObjectNode attributes;
}
