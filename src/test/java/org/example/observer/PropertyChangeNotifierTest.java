package org.example.observer;

import org.example.model.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para PropertyChangeNotifier (padrão Observer).
 *
 * Valida a inscrição, desincrição e notificação de observadores.
 *
 * @version 1.0
 * @author Property Management System
 */
@DisplayName("Testes do Observer Pattern - PropertyChangeNotifier")
class PropertyChangeNotifierTest {

    private PropertyChangeNotifier notifier;

    @Mock
    private PropertyListener mockListener;

    private Property testProperty;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        notifier = new PropertyChangeNotifier();
        testProperty = new Property("Rua Teste, 100, Ipiranga", new BigDecimal("9999.99"));
    }

    @Test
    @DisplayName("Deve inscrever um listener")
    void testSubscribe() {
        notifier.subscribe(mockListener);
        assertEquals(1, notifier.getListenerCount());
    }

    @Test
    @DisplayName("Não deve inscrever listener null")
    void testSubscribeNull() {
        notifier.subscribe(null);
        assertEquals(0, notifier.getListenerCount());
    }

    @Test
    @DisplayName("Não deve inscrever o mesmo listener duas vezes")
    void testSubscribeDuplicate() {
        notifier.subscribe(mockListener);
        notifier.subscribe(mockListener);
        assertEquals(1, notifier.getListenerCount());
    }

    @Test
    @DisplayName("Deve desinscrever um listener")
    void testUnsubscribe() {
        notifier.subscribe(mockListener);
        notifier.unsubscribe(mockListener);
        assertEquals(0, notifier.getListenerCount());
    }

    @Test
    @DisplayName("Deve limpar todos os listeners")
    void testUnsubscribeAll() {
        notifier.subscribe(mockListener);
        notifier.subscribe(mock(PropertyListener.class));
        notifier.unsubscribeAll();
        assertEquals(0, notifier.getListenerCount());
    }

    @Test
    @DisplayName("Deve notificar listeners sobre mudança de preço")
    void testNotifyPriceChange() {
        notifier.subscribe(mockListener);
        double oldPrice = 9000.00;
        double newPrice = 10000.00;

        notifier.notifyPriceChange(testProperty, oldPrice, newPrice);

        verify(mockListener, times(1)).onPriceChanged(testProperty, oldPrice, newPrice);
    }

    @Test
    @DisplayName("Deve notificar múltiplos listeners sobre mudança de preço")
    void testNotifyMultipleListeners() {
        PropertyListener listener2 = mock(PropertyListener.class);
        notifier.subscribe(mockListener);
        notifier.subscribe(listener2);

        notifier.notifyPriceChange(testProperty, 9000, 10000);

        verify(mockListener, times(1)).onPriceChanged(testProperty, 9000, 10000);
        verify(listener2, times(1)).onPriceChanged(testProperty, 9000, 10000);
    }

    @Test
    @DisplayName("Deve notificar listeners sobre criação de propriedade")
    void testNotifyPropertyCreated() {
        notifier.subscribe(mockListener);

        notifier.notifyPropertyCreated(testProperty);

        verify(mockListener, times(1)).onPropertyCreated(testProperty);
    }

    @Test
    @DisplayName("Deve notificar listeners sobre remoção de propriedade")
    void testNotifyPropertyRemoved() {
        notifier.subscribe(mockListener);

        notifier.notifyPropertyRemoved(testProperty);

        verify(mockListener, times(1)).onPropertyRemoved(testProperty);
    }

    @Test
    @DisplayName("Deve retornar lista de listeners")
    void testGetListeners() {
        notifier.subscribe(mockListener);
        PropertyListener listener2 = mock(PropertyListener.class);
        notifier.subscribe(listener2);

        assertEquals(2, notifier.getListeners().size());
        assertTrue(notifier.getListeners().contains(mockListener));
        assertTrue(notifier.getListeners().contains(listener2));
    }

    @Test
    @DisplayName("Não deve notificar após desinscrição")
    void testNoNotificationAfterUnsubscribe() {
        notifier.subscribe(mockListener);
        notifier.unsubscribe(mockListener);

        notifier.notifyPriceChange(testProperty, 9000, 10000);

        verify(mockListener, never()).onPriceChanged(testProperty, 9000, 10000);
    }
}
