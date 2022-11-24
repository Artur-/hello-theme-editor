import { LitElement, html } from 'lit';
import { customElement } from 'lit/decorators.js';

@customElement('login-to-access')
export class LoginToAccess extends LitElement {
  listeners: Record<string, EventListener> = {};

  render() {
    return html`
      <div
        style="position: absolute; top: 0; bottom: 0; left: 0; right: 0; z-index: 100000; background: var(--dev-tools-background-color-active-blurred); display: flex; align-items: center; justify-content: center; backdrop-filter: blur(2px);">
        <div>
          <a href="" style="color: var(--dev-tools-text-color); font-weight: 600;">Log in</a>
          or
          <a href="" style="color: var(--dev-tools-text-color); font-weight: 600;">sign up</a>
          to use the theme editor.
        </div>
      </div>
    `;
  }

  connectedCallback(): void {
    super.connectedCallback();

    ['focus', 'click', 'keydown', 'keyup', 'keypress'].forEach((event) => {
      const listener = (e: any) => {
        const container = this.parentElement || (this.parentNode as any)?.host;
        if (!container) {
          return;
        }
        if (this.contains(e.target) || (e.composed && e.composedPath().includes(this))) {
          return;
        }
        if (container.contains(e.target) || (e.composed && e.composedPath().includes(container))) {
          e.preventDefault();
          e.stopPropagation();
          if (event === 'focus') {
            e.target.blur();
          }
        }
      };
      document.body.addEventListener(event, listener, { capture: true });
      this.listeners[event] = listener;
    });
  }

  disconnectedCallback(): void {
    super.disconnectedCallback();

    Object.keys(this.listeners).forEach((event) => {
      document.body.removeEventListener(event, this.listeners[event]);
    });
    this.listeners = {};
  }
}