package com.unibg.magellanus.backend.route.service.distance;

import java.util.List;

import com.unibg.magellanus.backend.route.service.Coordinates;

public interface MatrixAPI {
	public MatrixResponse getDistances(List<Coordinates> coordinates);
}
