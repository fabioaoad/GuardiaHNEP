package bbp;

import bbp.util.JsfUtil;
import entity.AdministrarMedicamentos;
import entity.Medicamentos;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sbp.MedicamentosFacade;

@Named(value = "administrarMedicamentosController")
@ViewScoped
public class AdministrarMedicamentosController extends AbstractController<AdministrarMedicamentos> {

    @Inject
    private EnfermeroController dniEnfermController;
    @Inject
    private PacienteController pacienteController;
    @Inject
    private MedicamentosController medicamentosController;
    private List<Medicamentos> lst_m = new ArrayList();
    @EJB
    private sbp.MedicamentosFacade ejbFacade;

    public AdministrarMedicamentosController() {
        // Inform the Abstract parent controller of the concrete AdministrarMedicamentos?cap_first Entity
        super(AdministrarMedicamentos.class);
    }

    @Override
    protected void setEmbeddableKeys() {
        this.getSelected().getAdministrarMedicamentosPK().setIdMedicamento(this.getSelected().getMedicamentos().getCodIdMedicamento());
        this.getSelected().getAdministrarMedicamentosPK().setDniPac(this.getSelected().getPaciente().getDNIPac());
    }

    @Override
    protected void initializeEmbeddableKey() {
        this.getSelected().setAdministrarMedicamentosPK(new entity.AdministrarMedicamentosPK());
    }

    /**
     * Resets the "selected" attribute of any parent Entity controllers.
     */
    public void resetParents() {
        dniEnfermController.setSelected(null);
        pacienteController.setSelected(null);
        medicamentosController.setSelected(null);
    }

    /**
     * Sets the "selected" attribute of the Enfermero controller in order to
     * display its data in a dialog. This is reusing existing the existing View
     * dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareDniEnferm(ActionEvent event) {
        if (this.getSelected() != null && dniEnfermController.getSelected() == null) {
            dniEnfermController.setSelected(this.getSelected().getDniEnferm());
        }
    }

    /**
     * Sets the "selected" attribute of the Paciente controller in order to
     * display its data in a dialog. This is reusing existing the existing View
     * dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void preparePaciente(ActionEvent event) {
        if (this.getSelected() != null && pacienteController.getSelected() == null) {
            pacienteController.setSelected(this.getSelected().getPaciente());
        }
    }

    /**
     * Sets the "selected" attribute of the Medicamentos controller in order to
     * display its data in a dialog. This is reusing existing the existing View
     * dialog.
     *
     * @param event Event object for the widget that triggered an action
     */
    public void prepareMedicamentos(ActionEvent event) {
        if (this.getSelected() != null && medicamentosController.getSelected() == null) {
            medicamentosController.setSelected(this.getSelected().getMedicamentos());
        }
    }

    public void controlStock() {
        lst_m = ejbFacade.findAll();
        boolean ok = true;
        Iterator iter = lst_m.listIterator();
        while (iter.hasNext()) {
            Medicamentos m = (Medicamentos) iter.next();
            if (m.getCodIdMedicamento().equals(getSelected().getMedicamentos().getCodIdMedicamento())) {
                if (m.getStockMedicamentos() > 0) {
                   Integer stock = m.getStockMedicamentos() - 1; 
                   medicamentosController.prepareCreate(null);
                   medicamentosController.getSelected().setCodIdMedicamento(m.getCodIdMedicamento());
                   medicamentosController.getSelected().setNombreMedicamento(m.getNombreMedicamento());
                   medicamentosController.getSelected().setStockMedicamentos(stock);
                   medicamentosController.getSelected().setPresentacionTipo(m.getPresentacionTipo());
                   medicamentosController.save(null);
                } else {
                    JsfUtil.addErrorMessage("No hay stock del medicamneto");
                    ok = false;
                }
            }
        }
        if (ok) {
            Date ahora = new Date();
            getSelected().setFecha(ahora);
            saveNew(null);
        }
    }

    public List<Medicamentos> getLst_m() {
        return lst_m;
    }

    public void setLst_m(List<Medicamentos> lst_m) {
        this.lst_m = lst_m;
    }

    public MedicamentosFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(MedicamentosFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }
}
