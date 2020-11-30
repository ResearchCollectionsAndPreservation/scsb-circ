package org.recap.repository.jpa;

import org.recap.model.jpa.ImsLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rajeshbabuk on 25/Nov/2020
 */
public interface ImsLocationDetailsRepository extends JpaRepository<ImsLocationEntity, Integer> {
}