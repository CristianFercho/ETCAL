/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Entidades;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author USUARIO
 */
@Entity
@Table(name = "bus")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Bus.findAll", query = "SELECT b FROM Bus b"),
    @NamedQuery(name = "Bus.findByMatricula", query = "SELECT b FROM Bus b WHERE b.matricula = :matricula"),
    @NamedQuery(name = "Bus.findByEstado", query = "SELECT b FROM Bus b WHERE b.estado = :estado"),
    @NamedQuery(name = "Bus.findByAO", query = "SELECT b FROM Bus b WHERE b.aO = :aO"),
    @NamedQuery(name = "Bus.findByFabricante", query = "SELECT b FROM Bus b WHERE b.fabricante = :fabricante"),
    @NamedQuery(name = "Bus.findByCapacidad", query = "SELECT b FROM Bus b WHERE b.capacidad = :capacidad"),
    @NamedQuery(name = "Bus.findByCilindrinaje", query = "SELECT b FROM Bus b WHERE b.cilindrinaje = :cilindrinaje"),
    @NamedQuery(name = "Bus.findByChasis", query = "SELECT b FROM Bus b WHERE b.chasis = :chasis"),
    @NamedQuery(name = "Bus.findByTipoBus", query = "SELECT b FROM Bus b WHERE b.tipoBus = :tipoBus")})
public class Bus implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "matricula")
    private String matricula;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "estado")
    private String estado;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "a\ufffdo")
    private String aO;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "fabricante")
    private String fabricante;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "capacidad")
    private String capacidad;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "cilindrinaje")
    private String cilindrinaje;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "chasis")
    private String chasis;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "tipo_bus")
    private String tipoBus;

    public Bus() {
    }

    public Bus(String matricula) {
        this.matricula = matricula;
    }

    public Bus(String matricula, String estado, String aO, String fabricante, String capacidad, String cilindrinaje, String chasis, String tipoBus) {
        this.matricula = matricula;
        this.estado = estado;
        this.aO = aO;
        this.fabricante = fabricante;
        this.capacidad = capacidad;
        this.cilindrinaje = cilindrinaje;
        this.chasis = chasis;
        this.tipoBus = tipoBus;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAO() {
        return aO;
    }

    public void setAO(String aO) {
        this.aO = aO;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }

    public String getCilindrinaje() {
        return cilindrinaje;
    }

    public void setCilindrinaje(String cilindrinaje) {
        this.cilindrinaje = cilindrinaje;
    }

    public String getChasis() {
        return chasis;
    }

    public void setChasis(String chasis) {
        this.chasis = chasis;
    }

    public String getTipoBus() {
        return tipoBus;
    }

    public void setTipoBus(String tipoBus) {
        this.tipoBus = tipoBus;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (matricula != null ? matricula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Bus)) {
            return false;
        }
        Bus other = (Bus) object;
        if ((this.matricula == null && other.matricula != null) || (this.matricula != null && !this.matricula.equals(other.matricula))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entidades.Bus[ matricula=" + matricula + " ]";
    }
    
}
