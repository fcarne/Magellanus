package com.unibg.magellanus.backend.itinerary.service.impl;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unibg.magellanus.backend.itinerary.service.Itinerary;
import com.unibg.magellanus.backend.itinerary.service.ItineraryRepository;
import com.unibg.magellanus.backend.itinerary.service.ItineraryService;

@Service
public class ItineraryServiceImpl implements ItineraryService {

	ItineraryRepository repository;

	@Autowired
	public ItineraryServiceImpl(ItineraryRepository repository) {
		this.repository = repository;
	}

	@Override
	public Itinerary findById(String id) throws NoSuchElementException {
		return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Requested itinerary not found"));
	}

	@Override
	public Itinerary create(Itinerary itinerary) {
		return repository.save(itinerary);
	}

	@Override
	public Itinerary update(Itinerary itinerary, String issuer) throws IllegalStateException {
		if (!itinerary.getOwner().equals(issuer))
			throw new IllegalStateException("Issuer is not the owner of the itinerary");
		return repository.save(itinerary);
	}

	@Override
	public void delete(String id, String issuer) throws IllegalStateException {
		repository.findById(id).ifPresent(i -> {
			if (!i.getOwner().equals(issuer))
				throw new IllegalStateException("Issuer is not the owner of the itinerary");
			repository.deleteById(id);
		});
	}

	@Override
	public Iterable<Itinerary> findAll(String owner, Boolean completed) {
		if (completed != null)
			return completed ? repository.findByOwnerAndCompletionDateNotNull(owner)
					: repository.findByOwnerAndCompletionDateIsNull(owner);
		else
			return repository.findByOwner(owner);
	}

}
