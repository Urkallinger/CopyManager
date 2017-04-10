package de.urkallinger.copymanager.database;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.urkallinger.copymanager.model.Extension;
import de.urkallinger.copymanager.utils.ListUtils;

public class DataManager {

	public List<Extension> getExtensions() {
		EntityManager em = null;
		try {
			DatabaseHelper dbHelper = DatabaseHelper.getInstance();
			em = dbHelper.getEntityManager();
			Query q = em.createQuery("SELECT e FROM Extension e");
			List<Extension> extensions = ListUtils.castList(Extension.class, q.getResultList());	
			return extensions;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}
