import { api } from '../../../components/utils/api.js';

test('api instance has correct baseURL', () => {
  expect(api.defaults.baseURL).toBe('http://54.228.78.4:9192/api/v1');
});
