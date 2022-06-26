{
  description = "A CLI for adding rainbow parentheses to streaming input";

  # Nixpkgs / NixOS version to use.
  inputs.nixpkgs.url = "nixpkgs/nixos-21.11";

  outputs = { self, nixpkgs }:
    let

      # Generate a user-friendly version number.
      version = builtins.substring 0 8 self.lastModifiedDate;

      # System types to support.
      supportedSystems = [ "x86_64-linux" "x86_64-darwin" "aarch64-linux" "aarch64-darwin" ];

      # Helper function to generate an attrset '{ x86_64-linux = f "x86_64-linux"; ... }'.
      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;

      # Nixpkgs instantiated for supported system types.
      nixpkgsFor = forAllSystems (system: import nixpkgs { inherit system; });

    in
    {

      # Provide some binary packages for selected system types.
      # A single package will automatically be the default package
      packages = forAllSystems
        (system:
          let
            pkgs = nixpkgsFor.${system};
            graalvm = pkgs.graalvm11-ce;
            sbt = pkgs.sbt.override { jre = pkgs.openjdk11; };
            nodejs = pkgs.nodejs-17_x;
          in
          {
            rainbow-parens = pkgs.stdenv.mkDerivation
              {
                name = "rainbow-parens";
                buildInputs = [ sbt graalvm ];
                src = ./.;
              };

            sbt = sbt;
            graalvm = graalvm;
            nodejs = nodejs;
          });
      devShell = forAllSystems (system:
        let pkgs = nixpkgsFor.${system};
        in
        pkgs.mkShell {
          buildInputs = with pkgs; [
            self.packages.${system}.sbt
            self.packages.${system}.graalvm
            self.packages.${system}.nodejs
          ];
        });

    };
}

