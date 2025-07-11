import { api } from '../../../components/utils/api.js';

test('api instance has correct baseURL', () => {
  expect(api.defaults.baseURL).toBe('http://34.246.135.101:9192/api/v1');
});
