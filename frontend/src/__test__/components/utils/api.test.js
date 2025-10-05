import { api } from '../../../components/utils/api.js';

test('api instance has correct baseURL', () => {
  // Test should check for the expected base URL (ALB endpoint for production)
  // or allow environment-specific configuration
  const expectedBaseURL = process.env.REACT_APP_API_BASE_URL || "http://alb-pet-384183543.eu-west-1.elb.amazonaws.com/api/v1";
  expect(api.defaults.baseURL).toBe(expectedBaseURL);
});
