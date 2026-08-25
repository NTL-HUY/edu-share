// $lib/graphql/client.ts
import { GraphQLClient } from 'graphql-request';
import { getSdk } from '$lib/generated/sdk';
import { PUBLIC_GRAPHQL_URL } from '$env/static/public';

export function createSdk(fetchFn: typeof fetch) {
  const client = new GraphQLClient(PUBLIC_GRAPHQL_URL, { fetch: fetchFn });
  return getSdk(client);
}

export function getClientSdk() {
  const url = typeof window !== 'undefined' 
    ? `${window.location.origin}/api/graphql` 
    : '/api/graphql';

  const client = new GraphQLClient(url);
  return getSdk(client);
}