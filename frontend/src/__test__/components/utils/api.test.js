import {
  api,
  API_BASE_URL,
  FALLBACK_API_BASE_URL,
} from '../../../components/utils/api.js';

test('api instance has correct baseURL', () => {
  expect(API_BASE_URL).toBe(FALLBACK_API_BASE_URL);
  expect(api.defaults.baseURL).toBe(FALLBACK_API_BASE_URL);
});
