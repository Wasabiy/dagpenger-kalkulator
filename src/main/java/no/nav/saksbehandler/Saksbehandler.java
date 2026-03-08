package no.nav.saksbehandler;

import java.util.ArrayList;
import java.util.List;

import no.nav.dagpenger.DagpengerKalkulator;

public class Saksbehandler {

    private final String behandlerId;
    private final Vedtak vedtakSpesialisering;
    private final List<Sak> saker;
    private final DagpengerKalkulator dagpengerKalkulator;

    public Saksbehandler(String behandlerId, Vedtak vedtakSpesialisering) {
        if (behandlerId == null || behandlerId.isEmpty()) {
            throw new IllegalArgumentException("BehandlerId kan ikke være null eller tom");
        }
        if (vedtakSpesialisering == null) {
            throw new IllegalArgumentException("VedtakSpesialisering kan ikke være null");
        }
        if (vedtakSpesialisering == Vedtak.IKKE_SATT) {
            throw new IllegalArgumentException("VedtakSpesialisering kan ikke være IKKE_SATT");
        }
        this.vedtakSpesialisering = vedtakSpesialisering;
        this.behandlerId = behandlerId;
        this.saker = new ArrayList<>();
        this.dagpengerKalkulator = new DagpengerKalkulator();
    }

    public void seOversiktOverSaker() {
        System.out.println("Saksbehandler " + this.behandlerId + " har følgende saker:");
        for (Sak sak : saker) {
            System.out.printf(
                    "Sak %d (%s): vedtak=%s, behandlingsstatus=%s, dagsats=%.0f%n",
                    sak.getId(),
                    sak.getNavn(),
                    sak.getVedtak(),
                    sak.getBehandlingsstatus(),
                    sak.getDagsats());
        }
    }

    public Sak finnSak(long sakId, List<Sak> saker) {
        for (Sak sak : saker) {
            if (sak.getId() == sakId) {
                return sak;
            }
        }
        throw new IllegalArgumentException("Sak med id " + sakId + " ikke funnet.");
    }

    public List<Sak> hentUbehandledeSakerInnenforSpesialisering(List<Sak> saker) {
        if (saker == null) {
            throw new IllegalArgumentException("Saksliste kan ikke være null");
        }
        List<Sak> ubehandledeSaker = new ArrayList<>();
        for (Sak sak : saker) {
            if (verifiserAtSakKanBehandles(sak)) {
                 ubehandledeSaker.add(sak);
            } else {
                System.out.println("Sak " + sak.getId() + " kan ikke behandles av saksbehandler " + this.behandlerId);
            }

        }
        return ubehandledeSaker;
    }

    public boolean dobbeltsjekkBeregning(Sak sak, boolean inkluderVedtak) {
        if (sak == null) {
            throw new IllegalArgumentException("Sak kan ikke være null");
        }

        double registrertDagsats = sak.getDagsats();
        Vedtak registrertVedtak = sak.getVedtak();

        double forventetDagsats = dagpengerKalkulator.kalkulerDagsats(sak);
        Vedtak forventetVedtak = sak.getVedtak();

        sak.setDagsats(registrertDagsats);
        sak.setVedtak(registrertVedtak);

        if (Double.compare(forventetDagsats, registrertDagsats) != 0) {
            return false;
        } else if (inkluderVedtak && forventetVedtak != registrertVedtak) {
            return false;
        }
        return true;
    }

    public void godkjennSak(Sak sak) {
        if (!verifiserAtSakKanBehandles(sak)) {
            throw new IllegalStateException(
                    "Saken kan ikke behandles. Sjekk at den er ubehandlet og at vedtaket matcher saksbehandlerens spesialisering.");
        }
        if (this.vedtakSpesialisering == Vedtak.INNVILGET) {
            sak.setVedtak(Vedtak.INNVILGET);
            sak.setBehandlingsstatus(Behandlingsstatus.FERDIG);
            System.out.println("Behandler " + this.behandlerId + " har godkjent sak " + sak.getId() + " med vedtak "
                    + sak.getVedtak() + " og dagsats " + sak.getDagsats());
            return;
        } else if (this.vedtakSpesialisering == Vedtak.INNVILGET_MED_MAKSSATS) {
            sak.setVedtak(Vedtak.INNVILGET_MED_MAKSSATS);
            sak.setBehandlingsstatus(Behandlingsstatus.FERDIG);
            System.out.println("Behandler " + this.behandlerId + " har godkjent sak " + sak.getId() + " med vedtak "
                    + sak.getVedtak() + " og dagsats " + sak.getDagsats());
            return;
        }
        throw new IllegalStateException("Avslagssaker skal behandles med avslåSak.");
    }

    public void avslåSak(Sak sak) {
        if (!verifiserAtSakKanBehandles(sak)) {
            throw new IllegalStateException(
                    "Saken kan ikke behandles. Sjekk at den er ubehandlet og at vedtaket matcher saksbehandlerens spesialisering.");
        }
        sak.setVedtak(Vedtak.AVSLAG);
        sak.setBehandlingsstatus(Behandlingsstatus.FERDIG);
        System.out.println("Behandler " + this.behandlerId + " har avslått sak " + sak.getId() + " med vedtak "
                + sak.getVedtak() + " og dagsats " + sak.getDagsats());
    }

    public void leggTilSak(Sak sak) {
        verifiserAtSakKanBehandles(sak);
        if (!this.saker.contains(sak)) {
            this.saker.add(sak);
        }
    }

    public String getBehandlerId() {
        return behandlerId;
    }

    public Vedtak getVedtakSpesialisering() {
        return vedtakSpesialisering;
    }

    // Har lyst til å fjerne denne siden hvis dette skjer så har det skjedd noe galt
    // med systemet eller arbeidsflyten.
    private boolean verifiserAtSakKanBehandles(Sak sak) {
        if (sak == null) {
            return false;
        } else if (sak.getBehandlingsstatus() != Behandlingsstatus.UBEHANDLET) {
            return false;
        } else if (sak.getVedtak() != this.vedtakSpesialisering) {
            return false;
        }
        return true;
    }
}
