/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "tarjeta")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tarjeta.findAll", query = "SELECT t FROM Tarjeta t"),
    @NamedQuery(name = "Tarjeta.findByPin", query = "SELECT t FROM Tarjeta t WHERE t.pin = :pin"),
    @NamedQuery(name = "Tarjeta.findByCosto", query = "SELECT t FROM Tarjeta t WHERE t.costo = :costo"),
    @NamedQuery(name = "Tarjeta.findByTipo", query = "SELECT t FROM Tarjeta t WHERE t.tipo = :tipo"),
    @NamedQuery(name = "Tarjeta.findByEstado", query = "SELECT t FROM Tarjeta t WHERE t.estado = :estado"),
    @NamedQuery(name = "Tarjeta.findByPasajes", query = "SELECT t FROM Tarjeta t WHERE t.pasajes = :pasajes"),
    @NamedQuery(name = "Tarjeta.findByFecha", query = "SELECT t FROM Tarjeta t WHERE t.fecha = :fecha")})
public class Tarjeta implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "pin")
    private Integer pin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "costo")
    private int costo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "tipo")
    private String tipo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pasajes")
    private int pasajes;
    @Basic(optional = false)
    @NotNull
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tarjeta")
    private Recarga recarga;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pinTarjeta")
    private List<Usuario> usuarioList;
    @JoinColumn(name = "nombre_estacion", referencedColumnName = "nombre_estacion")
    @ManyToOne(optional = false)
    private Estacion nombreEstacion;

    public Tarjeta() {
    }

    public Tarjeta(Integer pin) {
        this.pin = pin;
    }

    public Tarjeta(Integer pin, int costo, String tipo, String estado, int pasajes, Date fecha) {
        this.pin = pin;
        this.costo = costo;
        this.tipo = tipo;
        this.estado = estado;
        this.pasajes = pasajes;
        this.fecha = fecha;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getPasajes() {
        return pasajes;
    }

    public void setPasajes(int pasajes) {
        this.pasajes = pasajes;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Recarga getRecarga() {
        return recarga;
    }

    public void setRecarga(Recarga recarga) {
        this.recarga = recarga;
    }

    @XmlTransient
    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    public void setUsuarioList(List<Usuario> usuarioList) {
        this.usuarioList = usuarioList;
    }

    public Estacion getNombreEstacion() {
        return nombreEstacion;
    }

    public void setNombreEstacion(Estacion nombreEstacion) {
        this.nombreEstacion = nombreEstacion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pin != null ? pin.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tarjeta)) {
            return false;
        }
        Tarjeta other = (Tarjeta) object;
        if ((this.pin == null && other.pin != null) || (this.pin != null && !this.pin.equals(other.pin))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Tarjeta[ pin=" + pin + " ]";
    }
    
}
