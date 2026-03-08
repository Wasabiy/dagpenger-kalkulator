package saksbehandler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import no.nav.saksbehandler.Behandlingsstatus;
import no.nav.saksbehandler.Sak;
import no.nav.saksbehandler.Saksbehandler;
import no.nav.saksbehandler.Vedtak;

public class SaksbehandlerTest {

    @Test
    public void hentUbehandledeSakerSkalKunReturnereUbehandlede() {
        Saksbehandler saksbehandler = new Saksbehandler("sb-1", Vedtak.INNVILGET);
        List<Sak> saker = new ArrayList<>();
        saker.addAll(List.of(
                opprettSak(1L, Vedtak.INNVILGET),
                opprettSak(2L, Vedtak.INNVILGET_MED_MAKSSATS),
                opprettSak(3L, Vedtak.AVSLAG)
        ));
        Sak ubehandletSak = opprettSak(1L, Vedtak.INNVILGET);
        Sak behandletSak = opprettSak(2L, Vedtak.INNVILGET);
        behandletSak.setBehandlingsstatus(Behandlingsstatus.FERDIG);
        behandletSak.setVedtak(Vedtak.INNVILGET_MED_MAKSSATS);

        saker.add(ubehandletSak);
        saker.add(behandletSak);

        List<Sak> ubehandledeSaker = saksbehandler.hentUbehandledeSakerInnenforSpesialisering(saker);
        assertEquals(2, ubehandledeSaker.size());
        assertEquals(ubehandletSak.getId(), ubehandledeSaker.get(0).getId());
    }

    @Test
    public void leggTilSakSkalFeileVedFeilSpesialisering() {
        Saksbehandler saksbehandler = new Saksbehandler("sb-2", Vedtak.AVSLAG);
        Sak sak = opprettSak(3L, Vedtak.INNVILGET);

        assertThrows(IllegalArgumentException.class, () -> saksbehandler.leggTilSak(sak));
    }

    @Test
    public void godkjennSakSkalSetteVedtakTilInnvilget() {
        Saksbehandler saksbehandler = new Saksbehandler("sb-3", Vedtak.INNVILGET);
        Sak sak = opprettSak(4L, Vedtak.INNVILGET);
        saksbehandler.leggTilSak(sak);

        saksbehandler.godkjennSak(sak);

        assertEquals(Vedtak.INNVILGET, sak.getVedtak());
        assertEquals(Behandlingsstatus.FERDIG, sak.getBehandlingsstatus());
    }

    @Test
    public void avslåSakSkalSetteVedtakTilAvslag() {
        Saksbehandler saksbehandler = new Saksbehandler("sb-4", Vedtak.AVSLAG);
        Sak sak = opprettSak(5L, Vedtak.AVSLAG);
        saksbehandler.leggTilSak(sak);

        saksbehandler.avslåSak(sak);

        assertEquals(Vedtak.AVSLAG, sak.getVedtak());
        assertEquals(Behandlingsstatus.FERDIG, sak.getBehandlingsstatus());
    }

    @Test
    public void kanIkkeBehandleSakToGanger() {
        Saksbehandler saksbehandler = new Saksbehandler("sb-5", Vedtak.INNVILGET);
        Sak sak = opprettSak(6L, Vedtak.INNVILGET);
        saksbehandler.leggTilSak(sak);
        saksbehandler.godkjennSak(sak);

        assertThrows(IllegalStateException.class, () -> saksbehandler.avslåSak(sak));
    }

    private Sak opprettSak(long id, Vedtak vedtak) {
        Sak sak = new Sak(id, "Testperson", 123456789);
        sak.setVedtak(vedtak);
        return sak;
    }
}
