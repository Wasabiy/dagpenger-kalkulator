package arbeidsflyt;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import no.nav.arbeidsflyt.Arbeidsflyt;
import no.nav.saksbehandler.Behandlingsstatus;
import no.nav.saksbehandler.Sak;
import no.nav.saksbehandler.Saksbehandler;
import no.nav.saksbehandler.Vedtak;
import no.nav.årslønn.Årslønn;

public class ArbeidsflyTest {

    @Test
    public void arbeidsflytSkalRuteSakerTilRiktigSaksbehandlerOgKunneBehandleDem() {
        Sak innvilgetSak = opprettSak(
                1L,
                "Innvilget Person",
                111111111,
                List.of(
                        new Årslønn(2025, 500000),
                        new Årslønn(2024, 450000),
                        new Årslønn(2023, 400000)
                )
        );
        Sak makssatsSak = opprettSak(
                2L,
                "Makssats Person",
                222222222,
                List.of(
                        new Årslønn(2025, 1200000),
                        new Årslønn(2024, 100000),
                        new Årslønn(2023, 90000)
                )
        );
        Sak avslagSak = opprettSak(
                3L,
                "Avslag Person",
                333333333,
                List.of(
                        new Årslønn(2025, 80000),
                        new Årslønn(2024, 100000),
                        new Årslønn(2023, 70000)
                )
        );

        List<Sak> saker = new ArrayList<>();
        saker.add(innvilgetSak);
        saker.add(makssatsSak);
        saker.add(avslagSak);

        Arbeidsflyt arbeidsflyt = new Arbeidsflyt(saker);
        Saksbehandler innvilgetBehandler = new Saksbehandler("sb-innvilget", Vedtak.INNVILGET);
        Saksbehandler makssatsBehandler = new Saksbehandler("sb-maks", Vedtak.INNVILGET_MED_MAKSSATS);
        Saksbehandler avslagBehandler = new Saksbehandler("sb-avslag", Vedtak.AVSLAG);

        arbeidsflyt.leggTilSaksbehandler(innvilgetBehandler);
        arbeidsflyt.leggTilSaksbehandler(makssatsBehandler);
        arbeidsflyt.leggTilSaksbehandler(avslagBehandler);

        arbeidsflyt.behandleSaker();

        List<Sak> innvilgetSaker = innvilgetBehandler.hentUbehandledeSakerInnenforSpesialisering(saker);
        List<Sak> makssatsSaker = makssatsBehandler.hentUbehandledeSakerInnenforSpesialisering(saker);
        List<Sak> avslagSaker = avslagBehandler.hentUbehandledeSakerInnenforSpesialisering(saker);

        assertTrue(innvilgetSaker.stream().anyMatch(sak -> sak.getId() == innvilgetSak.getId()));
        assertTrue(makssatsSaker.stream().anyMatch(sak -> sak.getId() == makssatsSak.getId()));
        assertTrue(avslagSaker.stream().anyMatch(sak -> sak.getId() == avslagSak.getId()));

        innvilgetBehandler.godkjennSak(innvilgetSak);
        makssatsBehandler.godkjennSak(makssatsSak);
        avslagBehandler.avslåSak(avslagSak);

        assertEquals(Vedtak.INNVILGET, innvilgetSak.getVedtak());
        assertEquals(Vedtak.INNVILGET_MED_MAKSSATS, makssatsSak.getVedtak());
        assertEquals(Vedtak.AVSLAG, avslagSak.getVedtak());
        assertEquals(Behandlingsstatus.FERDIG, innvilgetSak.getBehandlingsstatus());
        assertEquals(Behandlingsstatus.FERDIG, makssatsSak.getBehandlingsstatus());
        assertEquals(Behandlingsstatus.FERDIG, avslagSak.getBehandlingsstatus());
    }

    @Test
    public void saksbehandlerSkalKunneHenteFraInaktivArbeidsflytOgDeretterBehandleSaker() {
        Sak innvilgetSak = opprettSak(
                10L,
                "Inaktiv Innvilget",
                444444444,
                List.of(
                        new Årslønn(2025, 500000),
                        new Årslønn(2024, 450000),
                        new Årslønn(2023, 400000)
                )
        );
        innvilgetSak.setVedtak(Vedtak.INNVILGET);

        Sak avslagSak = opprettSak(
                11L,
                "Inaktiv Avslag",
                555555555,
                List.of(
                        new Årslønn(2025, 80000),
                        new Årslønn(2024, 100000),
                        new Årslønn(2023, 70000)
                )
        );
        avslagSak.setVedtak(Vedtak.AVSLAG);

        List<Sak> saker = new ArrayList<>();
        saker.add(innvilgetSak);
        saker.add(avslagSak);

        Arbeidsflyt arbeidsflyt = new Arbeidsflyt(saker);
        Saksbehandler innvilgetBehandler = new Saksbehandler("sb-inaktiv-inn", Vedtak.INNVILGET);
        Saksbehandler avslagBehandler = new Saksbehandler("sb-inaktiv-avs", Vedtak.AVSLAG);

        // Inaktiv arbeidsflyt: vi kaller ikke arbeidsflyt.behandleSaker()
        List<Sak> innvilgetSaker = innvilgetBehandler.hentUbehandledeSakerInnenforSpesialisering(saker);
        List<Sak> avslagSaker = avslagBehandler.hentUbehandledeSakerInnenforSpesialisering(saker);

        assertEquals(1, innvilgetSaker.size());
        assertEquals(1, avslagSaker.size());
        assertEquals(innvilgetSak.getId(), innvilgetSaker.get(0).getId());
        assertEquals(avslagSak.getId(), avslagSaker.get(0).getId());

        innvilgetBehandler.godkjennSak(innvilgetSak);
        avslagBehandler.avslåSak(avslagSak);

        assertEquals(Vedtak.INNVILGET, innvilgetSak.getVedtak());
        assertEquals(Vedtak.AVSLAG, avslagSak.getVedtak());
        assertEquals(Behandlingsstatus.FERDIG, innvilgetSak.getBehandlingsstatus());
        assertEquals(Behandlingsstatus.FERDIG, avslagSak.getBehandlingsstatus());
    }

    private Sak opprettSak(long id, String navn, int fødselsnummer, List<Årslønn> årslønner) {
        Sak sak = new Sak(id, navn, fødselsnummer);
        sak.setÅrslønner(new ArrayList<>(årslønner));
        return sak;
    }
}
