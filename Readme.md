# gh Pages

https://benjamin-asdf.github.io/hdc-wolfram/

# Concept


-   build a HD/VSA framework for wolfram + wolframite

-   Inspirations:

    -   <https://torchhd.readthedocs.io/en/stable/>
    -   <https://www.hd-computing.com/software>

-   Re-cook HD/VSA papers;

    -   <https://github.com/IBM/neuro-vector-symbolic-architectures-raven>
    -   (integrate with existing neural net framework of wolfram)

-   Apply the approach and investige architectures / parameters etc. to

    -   arc-AGI (filter a 3x3 dataset?)
    -   copycat domain

-   Attempt to model \'analogical reasoning\' (see existing Lit)

## Mushroom body models?

-   make a HD version of mushrooom body models, compare to sparse
    distrubute memory, modern hopfield and transformer architectures

-   try those also as analogical reasoners

# Implementations

-   HD/VSA: MAP, block sparse, ...
-   resonator networks
-   Sparse Distrubuted Memory


# Build Docs

- Depends on a Quarto install
- Run `build-site` in build.clj

# Dev

setup clay on save: 


```
(defun my-setup-clay-save-hook ()
  (interactive)
  (add-hook
   'after-save-hook
   (defun my-clay-reload  ()
     (interactive)
     (cider-sync-tooling-eval
      (format "((requiring-resolve 'scicloj.clay.v2.api/make!)\n {:source-path \"%s\"})"
              (buffer-file-name))))
   nil
   t))
```
