/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Recarga;
import Entidades.Estacion;
import Entidades.Tarjeta;
import Entidades.Usuario;
import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author USUARIO
 */
public class TarjetaJpaController implements Serializable {

    public TarjetaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tarjeta tarjeta) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (tarjeta.getUsuarioList() == null) {
            tarjeta.setUsuarioList(new ArrayList<Usuario>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Recarga recarga = tarjeta.getRecarga();
            if (recarga != null) {
                recarga = em.getReference(recarga.getClass(), recarga.getPinTarjeta());
                tarjeta.setRecarga(recarga);
            }
            Estacion nombreEstacion = tarjeta.getNombreEstacion();
            if (nombreEstacion != null) {
                nombreEstacion = em.getReference(nombreEstacion.getClass(), nombreEstacion.getNombreEstacion());
                tarjeta.setNombreEstacion(nombreEstacion);
            }
            List<Usuario> attachedUsuarioList = new ArrayList<Usuario>();
            for (Usuario usuarioListUsuarioToAttach : tarjeta.getUsuarioList()) {
                usuarioListUsuarioToAttach = em.getReference(usuarioListUsuarioToAttach.getClass(), usuarioListUsuarioToAttach.getIdUsuario());
                attachedUsuarioList.add(usuarioListUsuarioToAttach);
            }
            tarjeta.setUsuarioList(attachedUsuarioList);
            em.persist(tarjeta);
            if (recarga != null) {
                Tarjeta oldTarjetaOfRecarga = recarga.getTarjeta();
                if (oldTarjetaOfRecarga != null) {
                    oldTarjetaOfRecarga.setRecarga(null);
                    oldTarjetaOfRecarga = em.merge(oldTarjetaOfRecarga);
                }
                recarga.setTarjeta(tarjeta);
                recarga = em.merge(recarga);
            }
            if (nombreEstacion != null) {
                nombreEstacion.getTarjetaList().add(tarjeta);
                nombreEstacion = em.merge(nombreEstacion);
            }
            for (Usuario usuarioListUsuario : tarjeta.getUsuarioList()) {
                Tarjeta oldPinTarjetaOfUsuarioListUsuario = usuarioListUsuario.getPinTarjeta();
                usuarioListUsuario.setPinTarjeta(tarjeta);
                usuarioListUsuario = em.merge(usuarioListUsuario);
                if (oldPinTarjetaOfUsuarioListUsuario != null) {
                    oldPinTarjetaOfUsuarioListUsuario.getUsuarioList().remove(usuarioListUsuario);
                    oldPinTarjetaOfUsuarioListUsuario = em.merge(oldPinTarjetaOfUsuarioListUsuario);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTarjeta(tarjeta.getPin()) != null) {
                throw new PreexistingEntityException("Tarjeta " + tarjeta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tarjeta tarjeta) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tarjeta persistentTarjeta = em.find(Tarjeta.class, tarjeta.getPin());
            Recarga recargaOld = persistentTarjeta.getRecarga();
            Recarga recargaNew = tarjeta.getRecarga();
            Estacion nombreEstacionOld = persistentTarjeta.getNombreEstacion();
            Estacion nombreEstacionNew = tarjeta.getNombreEstacion();
            List<Usuario> usuarioListOld = persistentTarjeta.getUsuarioList();
            List<Usuario> usuarioListNew = tarjeta.getUsuarioList();
            List<String> illegalOrphanMessages = null;
            if (recargaOld != null && !recargaOld.equals(recargaNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Recarga " + recargaOld + " since its tarjeta field is not nullable.");
            }
            for (Usuario usuarioListOldUsuario : usuarioListOld) {
                if (!usuarioListNew.contains(usuarioListOldUsuario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Usuario " + usuarioListOldUsuario + " since its pinTarjeta field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (recargaNew != null) {
                recargaNew = em.getReference(recargaNew.getClass(), recargaNew.getPinTarjeta());
                tarjeta.setRecarga(recargaNew);
            }
            if (nombreEstacionNew != null) {
                nombreEstacionNew = em.getReference(nombreEstacionNew.getClass(), nombreEstacionNew.getNombreEstacion());
                tarjeta.setNombreEstacion(nombreEstacionNew);
            }
            List<Usuario> attachedUsuarioListNew = new ArrayList<Usuario>();
            for (Usuario usuarioListNewUsuarioToAttach : usuarioListNew) {
                usuarioListNewUsuarioToAttach = em.getReference(usuarioListNewUsuarioToAttach.getClass(), usuarioListNewUsuarioToAttach.getIdUsuario());
                attachedUsuarioListNew.add(usuarioListNewUsuarioToAttach);
            }
            usuarioListNew = attachedUsuarioListNew;
            tarjeta.setUsuarioList(usuarioListNew);
            tarjeta = em.merge(tarjeta);
            if (recargaNew != null && !recargaNew.equals(recargaOld)) {
                Tarjeta oldTarjetaOfRecarga = recargaNew.getTarjeta();
                if (oldTarjetaOfRecarga != null) {
                    oldTarjetaOfRecarga.setRecarga(null);
                    oldTarjetaOfRecarga = em.merge(oldTarjetaOfRecarga);
                }
                recargaNew.setTarjeta(tarjeta);
                recargaNew = em.merge(recargaNew);
            }
            if (nombreEstacionOld != null && !nombreEstacionOld.equals(nombreEstacionNew)) {
                nombreEstacionOld.getTarjetaList().remove(tarjeta);
                nombreEstacionOld = em.merge(nombreEstacionOld);
            }
            if (nombreEstacionNew != null && !nombreEstacionNew.equals(nombreEstacionOld)) {
                nombreEstacionNew.getTarjetaList().add(tarjeta);
                nombreEstacionNew = em.merge(nombreEstacionNew);
            }
            for (Usuario usuarioListNewUsuario : usuarioListNew) {
                if (!usuarioListOld.contains(usuarioListNewUsuario)) {
                    Tarjeta oldPinTarjetaOfUsuarioListNewUsuario = usuarioListNewUsuario.getPinTarjeta();
                    usuarioListNewUsuario.setPinTarjeta(tarjeta);
                    usuarioListNewUsuario = em.merge(usuarioListNewUsuario);
                    if (oldPinTarjetaOfUsuarioListNewUsuario != null && !oldPinTarjetaOfUsuarioListNewUsuario.equals(tarjeta)) {
                        oldPinTarjetaOfUsuarioListNewUsuario.getUsuarioList().remove(usuarioListNewUsuario);
                        oldPinTarjetaOfUsuarioListNewUsuario = em.merge(oldPinTarjetaOfUsuarioListNewUsuario);
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
                Integer id = tarjeta.getPin();
                if (findTarjeta(id) == null) {
                    throw new NonexistentEntityException("The tarjeta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tarjeta tarjeta;
            try {
                tarjeta = em.getReference(Tarjeta.class, id);
                tarjeta.getPin();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tarjeta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Recarga recargaOrphanCheck = tarjeta.getRecarga();
            if (recargaOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tarjeta (" + tarjeta + ") cannot be destroyed since the Recarga " + recargaOrphanCheck + " in its recarga field has a non-nullable tarjeta field.");
            }
            List<Usuario> usuarioListOrphanCheck = tarjeta.getUsuarioList();
            for (Usuario usuarioListOrphanCheckUsuario : usuarioListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tarjeta (" + tarjeta + ") cannot be destroyed since the Usuario " + usuarioListOrphanCheckUsuario + " in its usuarioList field has a non-nullable pinTarjeta field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estacion nombreEstacion = tarjeta.getNombreEstacion();
            if (nombreEstacion != null) {
                nombreEstacion.getTarjetaList().remove(tarjeta);
                nombreEstacion = em.merge(nombreEstacion);
            }
            em.remove(tarjeta);
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

    public List<Tarjeta> findTarjetaEntities() {
        return findTarjetaEntities(true, -1, -1);
    }

    public List<Tarjeta> findTarjetaEntities(int maxResults, int firstResult) {
        return findTarjetaEntities(false, maxResults, firstResult);
    }

    private List<Tarjeta> findTarjetaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tarjeta.class));
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

    public Tarjeta findTarjeta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tarjeta.class, id);
        } finally {
            em.close();
        }
    }

    public int getTarjetaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tarjeta> rt = cq.from(Tarjeta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
