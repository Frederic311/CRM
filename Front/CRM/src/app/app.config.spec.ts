import { appConfig } from './app.config';

/**
 * The providers array is what bootstraps the application, and it is a literal
 * nobody reviews twice.
 *
 * provideAnimationsAsync() appeared in it twice. The duplicate did not fail, so
 * it survived, and it registered a second animation renderer for no reason.
 */
describe('appConfig', () => {
  it('declares no provider twice', () => {
    const providers = appConfig.providers ?? [];

    expect(providers.length).toBe(new Set(providers).size);
  });
});
