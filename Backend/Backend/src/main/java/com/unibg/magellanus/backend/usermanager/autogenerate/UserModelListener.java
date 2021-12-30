package com.unibg.magellanus.backend.usermanager.autogenerate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.unibg.magellanus.backend.usermanager.User;

@Component
public class UserModelListener extends AbstractMongoEventListener<User> {

	private CollectionSequenceService sequenceService;

	@Autowired
	public UserModelListener(CollectionSequenceService sequenceService) {
		this.sequenceService = sequenceService;
	}

	@Override
	public void onBeforeConvert(BeforeConvertEvent<User> event) {
		if (event.getSource().getId() < 1) {
			event.getSource().setId(sequenceService.generateSequence(User.SEQUENCE_NAME));
		}
	}

}