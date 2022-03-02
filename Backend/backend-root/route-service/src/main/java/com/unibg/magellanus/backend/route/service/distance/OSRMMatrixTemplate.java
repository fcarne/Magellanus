package com.unibg.magellanus.backend.route.service.distance;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.unibg.magellanus.backend.route.service.Coordinates;

@Service
public class OSRMMatrixTemplate implements MatrixAPI {

	RestTemplate restTemplate;

	public OSRMMatrixTemplate(RestTemplate restTemplate) {
		super();
		this.restTemplate = restTemplate;
	}

	@Override
	public MatrixResponse getDistances(List<Coordinates> coordinates) {
		StringBuilder stringBuilder = new StringBuilder();

		// costruisce l'url secondo le specifiche di OSMR
		coordinates.stream().forEachOrdered(it -> {
			stringBuilder.append(it.getLon()).append(",").append(it.getLat()).append(";");
		});
		stringBuilder.setLength(stringBuilder.length() - 1);
		// effettua la chiamata http
		return restTemplate.getForObject(
				"http://router.project-osrm.org/table/v1/foot/" + stringBuilder.toString() + "?annotations=distance",
				MatrixResponse.class);
	}

}
