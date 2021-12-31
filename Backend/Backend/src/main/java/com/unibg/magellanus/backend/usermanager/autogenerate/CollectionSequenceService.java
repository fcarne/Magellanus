package com.unibg.magellanus.backend.usermanager.autogenerate;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CollectionSequenceService {

	private MongoOperations mongoOperations;

	@Autowired
	public CollectionSequenceService(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	public long generateSequence(String seqName) {

		CollectionSequence counter = mongoOperations.findAndModify(query(where("_id").is(seqName)),
				new Update().inc("seq", 1), options().returnNew(true).upsert(true), CollectionSequence.class);
		return !Objects.isNull(counter) ? counter.getSeq() : 1;

	}
}