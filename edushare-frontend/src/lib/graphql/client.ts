// $lib/graphql/client.ts
import { GraphQLClient } from 'graphql-request';
import { getSdk } from '$lib/generated/sdk';
import { PUBLIC_GRAPHQL_URL } from '$env/static/public';

export function createSdk(fetchFn: typeof fetch) {
  const client = new GraphQLClient(PUBLIC_GRAPHQL_URL, { fetch: fetchFn });
  return getSdk(client);
}