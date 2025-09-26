describe('api base URL resolution', () => {
  afterEach(() => {
    delete process.env.VITE_API_BASE_URL;
    jest.resetModules();
  });

  test('falls back to default baseURL when env var is missing', async () => {
    const { api, resolveApiBaseUrl } = await import('../../../components/utils/api.js');
    expect(resolveApiBaseUrl()).toBe('http://localhost:9192/api/v1');
    expect(api.defaults.baseURL).toBe('http://localhost:9192/api/v1');
  });

  test('uses VITE_API_BASE_URL when provided', async () => {
    process.env.VITE_API_BASE_URL = 'http://localhost:9193/api/v1';
    const { api, resolveApiBaseUrl } = await import('../../../components/utils/api.js');
    expect(resolveApiBaseUrl()).toBe('http://localhost:9193/api/v1');
    expect(api.defaults.baseURL).toBe('http://localhost:9193/api/v1');
  });
});
