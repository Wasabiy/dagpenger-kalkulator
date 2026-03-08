package no.nav.saksbehandler;

import java.util.ArrayList;
import java.util.List;

import no.nav.årslønn.Årslønn;

public class Sak {
    private final long id;
    private final String navn;
    private final long fødselsnummer;
    private double dagsats;
    private Behandlingsstatus behandlingsstatus;
    private Vedtak vedtak;
    private List<Årslønn> årslønner;

    public Sak(long id, String name, long fødselsnummer) {
        this.id = id;
        this.navn = name;
        this.fødselsnummer = fødselsnummer;
        this.behandlingsstatus = Behandlingsstatus.UBEHANDLET;
        this.vedtak = Vedtak.IKKE_SATT;
        this.årslønner = new ArrayList<>();
    }

    public void setÅrslønner(List<Årslønn> årslønner) {
        this.årslønner = årslønner;
    }
    //Jeg vet at mutable lister ikke er ideelt, men for dagpengerkalkulatoren sin metode for å fortsatt eksistere ønsket jeg ikke å fjerne den. ideelt hadde jeg lagt en sorteringsmetode i Sak-klassen og gjort årslønner til en immutable liste, men har endret nok på tester allerede tenker jeg bør la det være for nå
    public List<Årslønn> getÅrslønner() {
        return årslønner;
    }

    public Behandlingsstatus getBehandlingsstatus() {
        return behandlingsstatus;
    }

    public void setBehandlingsstatus(Behandlingsstatus behandlingsstatus) {
        if (behandlingsstatus == null) {
            throw new IllegalArgumentException("Behandlingsstatus kan ikke være null");
        }
        this.behandlingsstatus = behandlingsstatus;
    }

    public Vedtak getVedtak() {
        return vedtak;
    }

    public void setVedtak(Vedtak vedtak) {
        if (vedtak == null) {
            throw new IllegalArgumentException("Vedtak kan ikke være null");
        }
        this.vedtak = vedtak;
    }

    public void addÅrslønn(Årslønn årslønn) {
        if (årslønn == null) {
            throw new IllegalArgumentException("Årslønn kan ikke være null");
        }
        this.årslønner.add(årslønn);
    }

    public void setDagsats(double dagsats) {
        this.dagsats = dagsats;
    }

    public double getDagsats() {
        return dagsats;
    }

    public long getId() {
        return id;
    }

    public String getNavn() {
        return navn;
    }

    public long getFødselsnummer() {
        return fødselsnummer;
    }

}
