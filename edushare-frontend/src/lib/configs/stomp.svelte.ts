import { Client, type IMessage, type StompSubscription } from '@stomp/stompjs';

type MessageCallback = (msg: IMessage) => void;

class StompService {
	private client: Client;

	private subscriptions = new Map<string, StompSubscription>();

	private pendingSubs = new Map<string, MessageCallback>();

	connected = $state(false);

	constructor() {
		this.client = new Client({
			brokerURL: 'ws://localhost:8080/ws-chat',
			reconnectDelay: 5000,
			heartbeatIncoming: 4000,
			heartbeatOutgoing: 4000,
			debug: import.meta.env.DEV ? (str) => console.log('[STOMP]', str) : () => {},

			onConnect: () => {
				this.connected = true;
				this.resubscribeAll();
			},

			onWebSocketClose: () => {
				this.connected = false;
				this.subscriptions.clear();
			},

			onStompError: (frame) => {
				console.error('STOMP error:', frame.headers['message'], frame.body);
			},

			onWebSocketError: (evt) => {
				console.error('WebSocket error:', evt);
			}
		});
	}

	private resubscribeAll() {
		for (const [destination, callback] of this.pendingSubs) {
			if (this.subscriptions.has(destination)) continue;
			const sub = this.client.subscribe(destination, callback);
			this.subscriptions.set(destination, sub);
		}
	}

	activate() {
		if (!this.client.active) this.client.activate();
	}

	deactivate() {
		this.client.deactivate();
		this.subscriptions.clear();
		this.pendingSubs.clear();
		this.connected = false;
	}

	subscribe(destination: string, callback: MessageCallback) {
		this.pendingSubs.set(destination, callback);

		if (this.client.connected && !this.subscriptions.has(destination)) {
			const sub = this.client.subscribe(destination, callback);
			this.subscriptions.set(destination, sub);
		}
	}

	unsubscribe(destination: string) {
		this.subscriptions.get(destination)?.unsubscribe();
		this.subscriptions.delete(destination);
		this.pendingSubs.delete(destination);
	}

	publish(destination: string, body: unknown) {
		if (!this.client.connected) {
			console.warn('Chưa connected, không thể publish:', destination);
			return;
		}
		this.client.publish({ destination, body: JSON.stringify(body) });
	}
}

export const stomp = new StompService();