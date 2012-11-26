/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Entidades.*;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author USUARIO
 */
public class EstacionJpaController implements Serializable {

    public EstacionJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estacion estacion) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (estacion.getRutaList() == null) {
            estacion.setRutaList(new ArrayList<Ruta>());
        }
        if (estacion.getTarjetaList() == null) {
            estacion.setTarjetaList(new ArrayList<Tarjeta>());
        }
        if (estacion.getSolicitudList() == null) {
            estacion.setSolicitudList(new ArrayList<Solicitud>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Empleado idEmpleado = estacion.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado = em.getReference(idEmpleado.getClass(), idEmpleado.getId());
                estacion.setIdEmpleado(idEmpleado);
            }
            List<Ruta> attachedRutaList = new ArrayList<Ruta>();
            for (Ruta rutaListRutaToAttach : estacion.getRutaList()) {
                rutaListRutaToAttach = em.getReference(rutaListRutaToAttach.getClass(), rutaListRutaToAttach.getNombreRuta());
                attachedRutaList.add(rutaListRutaToAttach);
            }
            estacion.setRutaList(attachedRutaList);
            List<Tarjeta> attachedTarjetaList = new ArrayList<Tarjeta>();
            for (Tarjeta tarjetaListTarjetaToAttach : estacion.getTarjetaList()) {
                tarjetaListTarjetaToAttach = em.getReference(tarjetaListTarjetaToAttach.getClass(), tarjetaListTarjetaToAttach.getPin());
                attachedTarjetaList.add(tarjetaListTarjetaToAttach);
            }
            estacion.setTarjetaList(attachedTarjetaList);
            List<Solicitud> attachedSolicitudList = new ArrayList<Solicitud>();
            for (Solicitud solicitudListSolicitudToAttach : estacion.getSolicitudList()) {
                solicitudListSolicitudToAttach = em.getReference(solicitudListSolicitudToAttach.getClass(), solicitudListSolicitudToAttach.getTicket());
                attachedSolicitudList.add(solicitudListSolicitudToAttach);
            }
            estacion.setSolicitudList(attachedSolicitudList);
            em.persist(estacion);
            if (idEmpleado != null) {
                idEmpleado.getEstacionList().add(estacion);
                idEmpleado = em.merge(idEmpleado);
            }
            for (Ruta rutaListRuta : estacion.getRutaList()) {
                rutaListRuta.getEstacionList().add(estacion);
                rutaListRuta = em.merge(rutaListRuta);
            }
            for (Tarjeta tarjetaListTarjeta : estacion.getTarjetaList()) {
                Estacion oldNombreEstacionOfTarjetaListTarjeta = tarjetaListTarjeta.getNombreEstacion();
                tarjetaListTarjeta.setNombreEstacion(estacion);
                tarjetaListTarjeta = em.merge(tarjetaListTarjeta);
                if (oldNombreEstacionOfTarjetaListTarjeta != null) {
                    oldNombreEstacionOfTarjetaListTarjeta.getTarjetaList().remove(tarjetaListTarjeta);
                    oldNombreEstacionOfTarjetaListTarjeta = em.merge(oldNombreEstacionOfTarjetaListTarjeta);
                }
            }
            for (Solicitud solicitudListSolicitud : estacion.getSolicitudList()) {
                Estacion oldNombreEstacionOfSolicitudListSolicitud = solicitudListSolicitud.getNombreEstacion();
                solicitudListSolicitud.setNombreEstacion(estacion);
                solicitudListSolicitud = em.merge(solicitudListSolicitud);
                if (oldNombreEstacionOfSolicitudListSolicitud != null) {
                    oldNombreEstacionOfSolicitudListSolicitud.getSolicitudList().remove(solicitudListSolicitud);
                    oldNombreEstacionOfSolicitudListSolicitud = em.merge(oldNombreEstacionOfSolicitudListSolicitud);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEstacion(estacion.getNombreEstacion()) != null) {
                throw new PreexistingEntityException("Estacion " + estacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estacion estacion) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estacion persistentEstacion = em.find(Estacion.class, estacion.getNombreEstacion());
            Empleado idEmpleadoOld = persistentEstacion.getIdEmpleado();
            Empleado idEmpleadoNew = estacion.getIdEmpleado();
            List<Ruta> rutaListOld = persistentEstacion.getRutaList();
            List<Ruta> rutaListNew = estacion.getRutaList();
            List<Tarjeta> tarjetaListOld = persistentEstacion.getTarjetaList();
            List<Tarjeta> tarjetaListNew = estacion.getTarjetaList();
            List<Solicitud> solicitudListOld = persistentEstacion.getSolicitudList();
            List<Solicitud> solicitudListNew = estacion.getSolicitudList();
            List<String> illegalOrphanMessages = null;
            for (Tarjeta tarjetaListOldTarjeta : tarjetaListOld) {
                if (!tarjetaListNew.contains(tarjetaListOldTarjeta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Tarjeta " + tarjetaListOldTarjeta + " since its nombreEstacion field is not nullable.");
                }
            }
            for (Solicitud solicitudListOldSolicitud : solicitudListOld) {
                if (!solicitudListNew.contains(solicitudListOldSolicitud)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Solicitud " + solicitudListOldSolicitud + " since its nombreEstacion field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idEmpleadoNew != null) {
                idEmpleadoNew = em.getReference(idEmpleadoNew.getClass(), idEmpleadoNew.getId());
                estacion.setIdEmpleado(idEmpleadoNew);
            }
            List<Ruta> attachedRutaListNew = new ArrayList<Ruta>();
            for (Ruta rutaListNewRutaToAttach : rutaListNew) {
                rutaListNewRutaToAttach = em.getReference(rutaListNewRutaToAttach.getClass(), rutaListNewRutaToAttach.getNombreRuta());
                attachedRutaListNew.add(rutaListNewRutaToAttach);
            }
            rutaListNew = attachedRutaListNew;
            estacion.setRutaList(rutaListNew);
            List<Tarjeta> attachedTarjetaListNew = new ArrayList<Tarjeta>();
            for (Tarjeta tarjetaListNewTarjetaToAttach : tarjetaListNew) {
                tarjetaListNewTarjetaToAttach = em.getReference(tarjetaListNewTarjetaToAttach.getClass(), tarjetaListNewTarjetaToAttach.getPin());
                attachedTarjetaListNew.add(tarjetaListNewTarjetaToAttach);
            }
            tarjetaListNew = attachedTarjetaListNew;
            estacion.setTarjetaList(tarjetaListNew);
            List<Solicitud> attachedSolicitudListNew = new ArrayList<Solicitud>();
            for (Solicitud solicitudListNewSolicitudToAttach : solicitudListNew) {
                solicitudListNewSolicitudToAttach = em.getReference(solicitudListNewSolicitudToAttach.getClass(), solicitudListNewSolicitudToAttach.getTicket());
                attachedSolicitudListNew.add(solicitudListNewSolicitudToAttach);
            }
            solicitudListNew = attachedSolicitudListNew;
            estacion.setSolicitudList(solicitudListNew);
            estacion = em.merge(estacion);
            if (idEmpleadoOld != null && !idEmpleadoOld.equals(idEmpleadoNew)) {
                idEmpleadoOld.getEstacionList().remove(estacion);
                idEmpleadoOld = em.merge(idEmpleadoOld);
            }
            if (idEmpleadoNew != null && !idEmpleadoNew.equals(idEmpleadoOld)) {
                idEmpleadoNew.getEstacionList().add(estacion);
                idEmpleadoNew = em.merge(idEmpleadoNew);
            }
            for (Ruta rutaListOldRuta : rutaListOld) {
                if (!rutaListNew.contains(rutaListOldRuta)) {
                    rutaListOldRuta.getEstacionList().remove(estacion);
                    rutaListOldRuta = em.merge(rutaListOldRuta);
                }
            }
            for (Ruta rutaListNewRuta : rutaListNew) {
                if (!rutaListOld.contains(rutaListNewRuta)) {
                    rutaListNewRuta.getEstacionList().add(estacion);
                    rutaListNewRuta = em.merge(rutaListNewRuta);
                }
            }
            for (Tarjeta tarjetaListNewTarjeta : tarjetaListNew) {
                if (!tarjetaListOld.contains(tarjetaListNewTarjeta)) {
                    Estacion oldNombreEstacionOfTarjetaListNewTarjeta = tarjetaListNewTarjeta.getNombreEstacion();
                    tarjetaListNewTarjeta.setNombreEstacion(estacion);
                    tarjetaListNewTarjeta = em.merge(tarjetaListNewTarjeta);
                    if (oldNombreEstacionOfTarjetaListNewTarjeta != null && !oldNombreEstacionOfTarjetaListNewTarjeta.equals(estacion)) {
                        oldNombreEstacionOfTarjetaListNewTarjeta.getTarjetaList().remove(tarjetaListNewTarjeta);
                        oldNombreEstacionOfTarjetaListNewTarjeta = em.merge(oldNombreEstacionOfTarjetaListNewTarjeta);
                    }
                }
            }
            for (Solicitud solicitudListNewSolicitud : solicitudListNew) {
                if (!solicitudListOld.contains(solicitudListNewSolicitud)) {
                    Estacion oldNombreEstacionOfSolicitudListNewSolicitud = solicitudListNewSolicitud.getNombreEstacion();
                    solicitudListNewSolicitud.setNombreEstacion(estacion);
                    solicitudListNewSolicitud = em.merge(solicitudListNewSolicitud);
                    if (oldNombreEstacionOfSolicitudListNewSolicitud != null && !oldNombreEstacionOfSolicitudListNewSolicitud.equals(estacion)) {
                        oldNombreEstacionOfSolicitudListNewSolicitud.getSolicitudList().remove(solicitudListNewSolicitud);
                        oldNombreEstacionOfSolicitudListNewSolicitud = em.merge(oldNombreEstacionOfSolicitudListNewSolicitud);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = estacion.getNombreEstacion();
                if (findEstacion(id) == null) {
                    throw new NonexistentEntityException("The estacion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Estacion estacion;
            try {
                estacion = em.getReference(Estacion.class, id);
                estacion.getNombreEstacion();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Tarjeta> tarjetaListOrphanCheck = estacion.getTarjetaList();
            for (Tarjeta tarjetaListOrphanCheckTarjeta : tarjetaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estacion (" + estacion + ") cannot be destroyed since the Tarjeta " + tarjetaListOrphanCheckTarjeta + " in its tarjetaList field has a non-nullable nombreEstacion field.");
            }
            List<Solicitud> solicitudListOrphanCheck = estacion.getSolicitudList();
            for (Solicitud solicitudListOrphanCheckSolicitud : solicitudListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estacion (" + estacion + ") cannot be destroyed since the Solicitud " + solicitudListOrphanCheckSolicitud + " in its solicitudList field has a non-nullable nombreEstacion field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Empleado idEmpleado = estacion.getIdEmpleado();
            if (idEmpleado != null) {
                idEmpleado.getEstacionList().remove(estacion);
                idEmpleado = em.merge(idEmpleado);
            }
            List<Ruta> rutaList = estacion.getRutaList();
            for (Ruta rutaListRuta : rutaList) {
                rutaListRuta.getEstacionList().remove(estacion);
                rutaListRuta = em.merge(rutaListRuta);
            }
            em.remove(estacion);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estacion> findEstacionEntities() {
        return findEstacionEntities(true, -1, -1);
    }

    public List<Estacion> findEstacionEntities(int maxResults, int firstResult) {
        return findEstacionEntities(false, maxResults, firstResult);
    }

    private List<Estacion> findEstacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estacion.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Estacion findEstacion(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estacion> rt = cq.from(Estacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
